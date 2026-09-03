package com.Calton.EDEN;

import com.badlogic.gdx.Game;

import screens.MenuScreen;

/** Ponto de entrada da aplicação EDEN — partilhado por todas as plataformas. */
public class EDEN extends Game {

    @Override
    public void create() {
        // Inicia o jogo no ecrã do Menu Principal
        setScreen(new MenuScreen(this));
    }
}
