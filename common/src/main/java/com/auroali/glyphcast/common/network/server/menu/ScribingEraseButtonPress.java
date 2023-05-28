package com.auroali.glyphcast.common.network.server.menu;

import com.auroali.glyphcast.common.menu.ScribingMenu;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ScribingEraseButtonPress extends NetworkMessage {

    public ScribingEraseButtonPress() {}

    public ScribingEraseButtonPress(FriendlyByteBuf buf) {}

    @Override
    public void encode(FriendlyByteBuf buf) {}

    @Override
    public void handleC2S(ServerPlayer player) {
        if(player.containerMenu instanceof ScribingMenu menu) {
            menu.eraseLast();
        }
    }
}
