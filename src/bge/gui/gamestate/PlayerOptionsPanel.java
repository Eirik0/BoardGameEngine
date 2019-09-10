package bge.gui.gamestate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import bge.igame.player.PlayerInfo;
import bge.igame.player.PlayerOptions;
import bge.igame.player.PlayerOptions.CPOptionIntRange;
import bge.igame.player.PlayerOptions.CPOptionStringArray;
import bge.igame.player.PlayerOptions.CPOptionValues;
import gt.component.IMouseTracker;
import gt.ecomponent.EBackground;
import gt.ecomponent.EComponent;
import gt.ecomponent.EComponentPanel;
import gt.ecomponent.EComponentPanelBuilder;
import gt.ecomponent.ETextLabel;
import gt.ecomponent.list.EComboBox;
import gt.ecomponent.list.EComponentLocation;
import gt.ecomponent.location.EGluedLocation;
import gt.ecomponent.location.GlueSide;
import gt.ecomponent.slider.ESlider;
import gt.gameentity.GameEntity;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.gameentity.UserInputHandler;
import gt.gamestate.UserInput;

public class PlayerOptionsPanel implements GameEntity, UserInputHandler {
    private static final int OPTION_BOX_HEIGHT = 20;
    private static final int OPTION_SLIDER_HEIGHT = 16;
    private static final int OPTION_PADDING = 5;

    private final EComponentLocation cl;

    private final IMouseTracker mouseTracker;
    private final IGameImageDrawer imageDrawer;

    private EComponentPanel cp;

    private final PlayerOptions playerOptions;
    private final Set<String> excludedOptions;
    private final PlayerInfo playerInfo;

    public PlayerOptionsPanel(EComponentLocation cl, IMouseTracker mouseTracker, IGameImageDrawer imageDrawer, PlayerOptions playerOptions) {
        this(cl, mouseTracker, imageDrawer, playerOptions, new PlayerInfo(), Collections.emptySet());
    }

    public PlayerOptionsPanel(EComponentLocation cl, IMouseTracker mouseTracker, IGameImageDrawer imageDrawer, PlayerOptions playerOptions,
            PlayerInfo playerInfo, Set<String> excludedOptions) {
        this.cl = cl;
        this.imageDrawer = imageDrawer;
        this.mouseTracker = mouseTracker;
        this.playerOptions = playerOptions;
        this.excludedOptions = excludedOptions;
        this.playerInfo = playerInfo;
        rebuildComponentPanel();
    }

    private void rebuildComponentPanel() {
        EComponentPanelBuilder panelBuilder = new EComponentPanelBuilder(mouseTracker)
                .add(0, new EBackground(cl, Color.BLACK));
        if (playerOptions != null) {
            List<EComponent> components = new ArrayList<>();
            createComponents(components, playerOptions, 0);
            for (int i = 0; i < components.size(); ++i) {
                panelBuilder.add(components.size() - i, components.get(i));
            }
        }
        cp = panelBuilder.build();
    }

    private double createComponents(List<EComponent> components, PlayerOptions options, double y) {
        CPOptionValues possibleValues = options.possibleValues;
        String key = possibleValues.getKey();
        if (excludedOptions.contains(key)) {
            return y;
        }
        EGluedLocation labelCL = cl.createGluedLocation(GlueSide.TOP, 0, y, -110, y + OPTION_BOX_HEIGHT);
        components.add(new ETextLabel(labelCL, options.optionName + ":", false, false));
        if (possibleValues instanceof CPOptionStringArray) {
            EGluedLocation optionsCl = cl.createGluedLocation(GlueSide.TOP, 90, y, 0, y + OPTION_BOX_HEIGHT);
            CPOptionStringArray values = (CPOptionStringArray) possibleValues;
            String option = playerInfo.getOption(key);
            int optionIndex = 0;
            if (option == null) {
                option = values.array[0];
                playerInfo.setOption(values.getKey(), values.array[0]);
            } else {
                for (int i = 0; i < values.array.length; ++i) {
                    if (option.equals(values.array[i])) {
                        optionIndex = i;
                        break;
                    }
                }
            }
            components.add(new EComboBox(optionsCl, imageDrawer, values.array, 5, optionIndex, i -> {
                playerInfo.setOption(values.getKey(), values.array[i]);
                rebuildComponentPanel();
            }));
            y += OPTION_BOX_HEIGHT + OPTION_PADDING;
            List<PlayerOptions> subOptions = options.subOptions.get(option);
            if (subOptions != null) {
                for (PlayerOptions subOption : subOptions) {
                    y = createComponents(components, subOption, y);
                }
            }
        } else if (possibleValues instanceof CPOptionIntRange) {
            EGluedLocation optionsCl = cl.createGluedLocation(GlueSide.TOP, 50, y, -50, y + OPTION_SLIDER_HEIGHT);
            CPOptionIntRange values = (CPOptionIntRange) possibleValues;
            Integer option = playerInfo.getOptionInt(key);
            if (option == null) {
                option = Integer.valueOf(values.minValue);
                playerInfo.setOption(values.getKey(), option);
            }
            ETextLabel valueLabel = new ETextLabel(optionsCl.createGluedLocation(GlueSide.RIGHT, 0, 0, 50, 0), option.toString(), false);
            components.add(new ESlider(optionsCl, values.minValue, values.maxValue, option.intValue(), i -> {
                Integer value = Integer.valueOf(i);
                playerInfo.setOption(values.getKey(), value);
                valueLabel.setText(value.toString());
            }));
            components.add(valueLabel);
            y += OPTION_SLIDER_HEIGHT + OPTION_PADDING;
        } else {
            throw new IllegalStateException("Unknown CPOptionValues: " + possibleValues);
        }
        return y;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    @Override
    public void update(double dt) {
        cp.update(dt);
    }

    @Override
    public void drawOn(IGraphics g) {
        cp.drawOn(g);
    }

    @Override
    public void handleUserInput(UserInput input) {
        cp.handleUserInput(input);
    }
}
