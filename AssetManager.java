package br.mackenzie;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class AssetManager {
    public Texture telaPretaImg;
    public Texture[] backgrounds = new Texture[3];
    public Texture[] playerBase = new Texture[3];
    public Texture[] playerGolpes = new Texture[3];
    public Texture[][] inimigoBaseNivel = new Texture[3][3];
    public Texture[][] inimigoGolpesNivel = new Texture[3][3];

    public Texture heartFull, heartEmpty;
    public Texture count3, count2, count1;
    public Texture[] barras = new Texture[7];
    public Texture venceuImg, perdeuImg;
    public Texture pressSpaceImg;

    public Texture playImg;
    public Texture restartImg;
    public Texture fundoMenuImg;
    public Texture cimaMenuImg;
    public Texture menuImg;

    public Texture starFull, starEmpty;
    public Texture[] numerosPontuacao = new Texture[10];
    public Texture pontoDecimal;

    public Music backgroundMusic;
    public Sound punchSound;

    public void carregarTexturas() {
        backgrounds[0] = new Texture("background1.jpg");
        backgrounds[1] = new Texture("background2.jpg");
        backgrounds[2] = new Texture("background3.jpg");
        telaPretaImg = new Texture("preto.png");

        heartFull = new Texture("heart_full.png");
        heartEmpty = new Texture("heart_empty.png");
        count3 = new Texture("3.png");
        count2 = new Texture("2.png");
        count1 = new Texture("1.png");

        for (int i = 0; i < 7; i++) {
            barras[i] = new Texture("barra" + (i + 1) + ".png");
        }

        venceuImg = new Texture("venceu.png");
        perdeuImg = new Texture("perdeu.png");
        pressSpaceImg = new Texture("pressspace.png");

        playImg = new Texture("play.png");
        restartImg = new Texture("restart.png");
        fundoMenuImg = new Texture("fundomenu.png");
        cimaMenuImg = new Texture("menucima.png");
        menuImg = new Texture("menu.png");

        starFull = new Texture("star.png");
        starEmpty = new Texture("starempty.png");

        for (int i = 0; i < 10; i++) {
            numerosPontuacao[i] = new Texture("num" + i + ".png");
        }

        pontoDecimal = new Texture("ponto.png");

        playerBase[0] = new Texture("player_1.png");
        playerBase[1] = new Texture("player_2.png");
        playerBase[2] = new Texture("player_3.png");
        playerGolpes[0] = new Texture("player_golpe1.png");
        playerGolpes[1] = new Texture("player_golpe2.png");
        playerGolpes[2] = new Texture("player_golpe3.png");

        carregarInimigos();
    }

    private void carregarInimigos() {
        String[] inimigos = {"enemy1", "enemy2", "enemy3"};

        for (int nivel = 0; nivel < 3; nivel++) {
            String prefixo = inimigos[nivel];
            inimigoBaseNivel[nivel][0] = new Texture(prefixo + "_1.png");
            inimigoBaseNivel[nivel][1] = new Texture(prefixo + "_2.png");
            inimigoBaseNivel[nivel][2] = new Texture(prefixo + "_3.png");
            inimigoGolpesNivel[nivel][0] = new Texture(prefixo + "_golpe1.png");
            inimigoGolpesNivel[nivel][1] = new Texture(prefixo + "_golpe2.png");
            inimigoGolpesNivel[nivel][2] = new Texture(prefixo + "_golpe3.png");
        }
    }

    public void carregarAudio() {
        backgroundMusic = com.badlogic.gdx.Gdx.audio.newMusic(com.badlogic.gdx.Gdx.files.internal("music.mp3"));
        punchSound = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("punch.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
    }

    public void dispose() {
        disposarArray(backgrounds);
        disposarArray(playerBase);
        disposarArray(playerGolpes);
        disposarArray(barras);
        disposarArray(numerosPontuacao);

        for (Texture[] array : inimigoBaseNivel) disposarArray(array);
        for (Texture[] array : inimigoGolpesNivel) disposarArray(array);

        disposarIndividual(heartFull, heartEmpty, count3, count2, count1,
            venceuImg, perdeuImg, pressSpaceImg, playImg,
            restartImg, fundoMenuImg, cimaMenuImg, menuImg,
            starFull, starEmpty, pontoDecimal);

        if (backgroundMusic != null) backgroundMusic.dispose();
        if (punchSound != null) punchSound.dispose();
        if (telaPretaImg != null) telaPretaImg.dispose();
    }

    private void disposarArray(Texture[] array) {
        if (array != null) {
            for (Texture t : array) {
                if (t != null) t.dispose();
            }
        }
    }

    private void disposarIndividual(Texture... textures) {
        for (Texture t : textures) {
            if (t != null) t.dispose();
        }
    }
}
