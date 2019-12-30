package eu.hansolo.tilesfx.skins;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.TextSize;
import eu.hansolo.tilesfx.TilesLocalization;
import eu.hansolo.tilesfx.fonts.Fonts;
import eu.hansolo.tilesfx.tools.Helper;
import eu.hansolo.tilesfx.tools.SunMoonCalculator;
import eu.hansolo.tilesfx.weather.DarkSky.ConditionAndIcon;
import eu.hansolo.tilesfx.weather.WeatherSymbol;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SunTileSkin extends TileSkin {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private SunMoonCalculator smc;
    private Text              titleText;
    private Text              text;

    private WeatherSymbol     sunriseSymbol;
    private Text              sunriseTitle;
    private Text              sunriseText;
    private HBox              sunriseBox;

    private WeatherSymbol     sunsetSymbol;
    private Text              sunsetTitle;
    private Text              sunsetText;
    private HBox              sunsetBox;

    private VBox              infoBox;


    // ******************** Constructors **************************************
    public SunTileSkin(final Tile TILE) {
        super(TILE);
    }


    // ******************** Initialization ************************************
    @Override protected void initGraphics() {
        super.initGraphics();

        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zdt = now.atZone(tile.getZoneId());

        try {
            smc = new SunMoonCalculator(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), tile.getCurrentLocation().getLatitude(), tile.getCurrentLocation().getLongitude());
            smc.calcEphemeris(tile.getZoneId());
        } catch (Exception e) {
            smc = null;
        }
        titleText = new Text();
        titleText.setFill(tile.getTitleColor());
        Helper.enableNode(titleText, !tile.getTitle().isEmpty());

        sunriseSymbol            = new WeatherSymbol(ConditionAndIcon.SUNRISE, 36, Tile.TileColor.YELLOW_ORANGE.color);
        sunriseTitle             = new Text(TilesLocalization.property("EphemerisTileSkin.SunriseLabel"));
        sunriseText              = new Text("--:--");
        VBox sunriseTextBox      = new VBox(sunriseTitle, sunriseText);
        sunriseTextBox.setAlignment(Pos.CENTER_LEFT);
        sunriseBox               = new HBox(sunriseSymbol, sunriseTextBox);
        sunriseBox.setAlignment(Pos.CENTER);

        sunsetSymbol             = new WeatherSymbol(ConditionAndIcon.SUNSET, 36, Tile.TileColor.DARK_BLUE.color);
        sunsetTitle              = new Text(TilesLocalization.property("EphemerisTileSkin.SunsetLabel"));
        sunsetText               = new Text("--:--");
        VBox sunsetTextBox       = new VBox(sunsetTitle, sunsetText);
        sunsetTextBox.setAlignment(Pos.CENTER_LEFT);
        sunsetBox                = new HBox(sunsetSymbol, sunsetTextBox);
        sunsetBox.setAlignment(Pos.CENTER);

        infoBox        = new VBox(sunriseBox, sunsetBox);
        infoBox.setAlignment(Pos.CENTER);

        text = new Text(tile.getText());
        Helper.enableNode(text, tile.isTextVisible());

        getPane().getChildren().addAll(titleText, text, infoBox);
    }

    @Override protected void registerListeners() {
        super.registerListeners();
    }


    // ******************** Methods *******************************************
    @Override protected void handleEvents(final String EVENT_TYPE) {
        super.handleEvents(EVENT_TYPE);

        if ("VISIBILITY".equals(EVENT_TYPE)) {
            Helper.enableNode(titleText, !tile.getTitle().isEmpty());
            Helper.enableNode(text, tile.isTextVisible());
        } else if ("RECALC".equals(EVENT_TYPE)) {
            calcEphemeris();
        }
    }

    private void calcEphemeris() {
        try { smc.setDate(LocalDate.now()); } catch (Exception e) {}
        smc.calcEphemeris(tile.getZoneId());

        sunriseText.setText(TIME_FORMATTER.format(smc.getSunrise()));
        sunsetText.setText(TIME_FORMATTER.format(smc.getSunset()));
    }


    // ******************** Resizing ******************************************
    @Override protected void resizeDynamicText() {
        double fontSize = size * TextSize.NORMAL.factor;
        Font   font     = Fonts.latoRegular(fontSize);

        sunriseText.setFont(font);
        sunsetText.setFont(font);

        fontSize = size * TextSize.BIGGER.factor;
        font = Fonts.latoRegular(fontSize);

        sunriseTitle.setFont(font);
        sunsetTitle.setFont(font);
    }
    @Override protected void resizeStaticText() {
        double maxWidth = width - size * 0.1;
        double fontSize = size * textSize.factor;

        boolean customFontEnabled = tile.isCustomFontEnabled();
        Font    customFont        = tile.getCustomFont();
        Font    font              = (customFontEnabled && customFont != null) ? Font.font(customFont.getFamily(), fontSize) : Fonts.latoRegular(fontSize);

        titleText.setFont(font);
        if (titleText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(titleText, maxWidth, fontSize); }
        switch(tile.getTitleAlignment()) {
            default    :
            case LEFT  : titleText.relocate(size * 0.05, size * 0.05); break;
            case CENTER: titleText.relocate((width - titleText.getLayoutBounds().getWidth()) * 0.5, size * 0.05); break;
            case RIGHT : titleText.relocate(width - (size * 0.05) - titleText.getLayoutBounds().getWidth(), size * 0.05); break;
        }

        text.setText(tile.getText());
        text.setFont(font);
        if (text.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(text, maxWidth, fontSize); }
        switch(tile.getTextAlignment()) {
            default    :
            case LEFT  : text.setX(size * 0.05); break;
            case CENTER: text.setX((width - text.getLayoutBounds().getWidth()) * 0.5); break;
            case RIGHT : text.setX(width - (size * 0.05) - text.getLayoutBounds().getWidth()); break;
        }
        text.setY(height - size * 0.05);
    }

    @Override protected void resize() {
        super.resize();

        sunriseSymbol.setPrefSize(size * 0.3, size * 0.3);
        sunsetSymbol.setPrefSize(size * 0.3, size * 0.3);

        sunriseBox.setSpacing(size * 0.025);
        sunsetBox.setSpacing(size * 0.025);

        infoBox.setSpacing(width * 0.15);

        infoBox.setPrefSize(contentBounds.getWidth(), contentBounds.getHeight());
        infoBox.relocate(contentBounds.getX(), contentBounds.getY());
    }

    @Override protected void redraw() {
        super.redraw();
        titleText.setText(tile.getTitle());
        sunriseText.setText(TIME_FORMATTER.format(smc.getSunrise()));
        sunsetText.setText(TIME_FORMATTER.format(smc.getSunset()));

        resizeDynamicText();
        resizeStaticText();

        titleText.setFill(tile.getTitleColor());
        text.setFill(tile.getTextColor());

        sunriseSymbol.setSymbolColor(tile.getTextColor());
        sunriseTitle.setFill(tile.getTextColor());
        sunriseText.setFill(tile.getTextColor());

        sunsetSymbol.setSymbolColor(tile.getTextColor());
        sunsetTitle.setFill(tile.getTextColor());
        sunsetText.setFill(tile.getTextColor());
    }
}

