/*
 * Requiem
 * Copyright (C) 2017-2021 Ladysnake
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses>.
 *
 * Linking this mod statically or dynamically with other
 * modules is making a combined work based on this mod.
 * Thus, the terms and conditions of the GNU General Public License cover the whole combination.
 *
 * In addition, as a special exception, the copyright holders of
 * this mod give you permission to combine this mod
 * with free software programs or libraries that are released under the GNU LGPL
 * and with code included in the standard release of Minecraft under All Rights Reserved (or
 * modified versions of such code, with unchanged license).
 * You may copy and distribute such a system following the terms of the GNU GPL for this mod
 * and the licenses of the other code concerned.
 *
 * Note that people who make modified versions of this mod are not obligated to grant
 * this special exception for their modified versions; it is their choice whether to do so.
 * The GNU General Public License gives permission to release a modified version without this exception;
 * this exception also makes it possible to release a modified version which carries forward this exception.
 */
package ladysnake.requiem.common.impl.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.requiem.api.v1.entity.InventoryLimiter;
import ladysnake.requiem.api.v1.entity.InventoryShape;
import ladysnake.requiem.api.v1.possession.PossessionComponent;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class PossessionInventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> {
    private final InventoryLimiter limiter;
    private final PossessionComponent possessionComponent;

    public PossessionInventoryScreen(PlayerEntity player) {
        super(player.playerScreenHandler, player.inventory, new TranslatableText("container.crafting"));
        this.limiter = InventoryLimiter.KEY.get(player);
        this.possessionComponent = PossessionComponent.get(player);
    }

    @Override
    protected void init() {
        if (!this.trySwapInventoryScreen()) {
            super.init();
            this.x = (this.width - this.backgroundWidth) / 2;
        }
    }

    public void tick() {
        this.trySwapInventoryScreen();
    }

    private boolean trySwapInventoryScreen() {
        assert this.client != null;
        assert this.client.interactionManager != null;
        assert this.client.player != null;

        if (this.client.interactionManager.hasCreativeInventory()) {
            this.client.openScreen(new CreativeInventoryScreen(this.client.player));
            return true;
        } else if (this.limiter.getInventoryShape() != InventoryShape.ALT_LARGE) {
            this.client.openScreen(new InventoryScreen(this.client.player));
            return true;
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        MobEntity possessedEntity = this.possessionComponent.getPossessedEntity();
        Text title = possessedEntity != null ? possessedEntity.getName() : this.title;
        this.textRenderer.draw(matrices, title, this.titleX, this.titleY, 0x404040);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        assert this.client != null;
        assert this.client.player != null;

        //noinspection deprecation
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        InventoryShape shape = this.limiter.getInventoryShape();
        this.client.getTextureManager().bindTexture(shape.swapBackground(BACKGROUND_TEXTURE));
        int x = this.x;
        int y = this.y;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        shape.setupEntityCrop(this.x, this.y);
        InventoryScreen.drawEntity(
            (int) shape.shiftEntityX(x + 51),
            (int) shape.shiftEntityY(y + 75),
            30,
            shape.shiftEntityX((float)(x + 51) - mouseX),
            shape.shiftEntityY((float)(y + 75 - 50) - mouseY),
            this.client.player
        );
        shape.tearDownEntityCrop();
    }
}
