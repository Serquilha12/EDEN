package mz.ac.ucm.eden;

import com.badlogic.gdx.Game;
import mz.ac.ucm.eden.screens.MenuScreen;

public class Main extends Game {
    @Override
    public void create() {
        // Inicia a aplicação no Menu Inicial
        setScreen(new MenuScreen(this));
    }
}
