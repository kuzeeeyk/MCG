package amerebagatelle.github.io.mcg.gui.screen;

import amerebagatelle.github.io.mcg.MCG;
import amerebagatelle.github.io.mcg.gui.MCGButtonWidget;
import amerebagatelle.github.io.mcg.gui.overlay.ErrorDisplayOverlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class CoordinateFileCreationScreen extends Screen {
    private TextFieldWidget fileNameWidget;
    private MCGButtonWidget confirmButton;
    private MCGButtonWidget cancelButton;

    private final String fileType;
    private final Path folderPath;

    public CoordinateFileCreationScreen(String fileType, Path folderPath) {
        super(new LiteralText("CoordinateFileCreationScreen"));
        this.fileType = fileType;
        this.folderPath = folderPath;
    }

    @Override
    public void init() {
        fileNameWidget = new TextFieldWidget(textRenderer, width / 2 - 100, 100, 200, 20, new TranslatableText("mcg.button.name"));
        this.addSelectableChild(fileNameWidget);

        confirmButton = new MCGButtonWidget(width / 2 - 50, height - 100, 100, 20, new TranslatableText("mcg.button.confirm"), press -> confirm());
        this.addDrawableChild(confirmButton);
        cancelButton = new MCGButtonWidget(width / 2 - 50, height - 70, 100, 20, new TranslatableText("mcg.button.cancel"), press -> cancel());
        this.addDrawableChild(cancelButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        updateButtonStates();
        this.renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, I18n.translate("mcg.file.new" + fileType), this.width / 2, 50, 16777215);
        fileNameWidget.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        ErrorDisplayOverlay.INSTANCE.render(matrices, height);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void confirm() {
        try {
            if (fileType.equals("folder")) {
                MCG.coordinatesManager.createFolder(Paths.get(folderPath.toString(), fileNameWidget.getText()));
            } else {
                MCG.coordinatesManager.initNewCoordinatesFile(Paths.get(folderPath.toString(), fileNameWidget.getText().endsWith(".coordinates") ? fileNameWidget.getText() : fileNameWidget.getText() + ".coordinates"));
            }
        } catch (IOException e) {
            MCG.logger.debug("Can't make new coordinates file.");
            ErrorDisplayOverlay.INSTANCE.addError(I18n.translate("mcg.file.creationfail"));
        } finally {
            Objects.requireNonNull(client).setScreen(new CoordinateFileManager());
        }
    }

    private void cancel() {
        Objects.requireNonNull(client).setScreen(new CoordinateFileManager());
    }

    private void updateButtonStates() {
        confirmButton.active = fileNameWidget.getText().length() != 0;
    }
}
