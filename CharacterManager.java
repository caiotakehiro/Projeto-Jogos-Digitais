package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;

public class CharacterManager {
    private AssetManager assets;
    private GameState state;

    public float playerX, playerYBase;
    public float enemyX, enemyYBase;

    public Texture playerAtual;
    public Texture enemyAtual;
    public Texture background;

    public Texture playerSpriteAntesDelay;
    public Texture enemySpriteAntesDelay;

    public Texture[] enemyBase;
    public Texture[] enemyGolpes;

    public CharacterManager(AssetManager assets, GameState state) {
        this.assets = assets;
        this.state = state;
        inicializarPosicoes();
    }

    private void inicializarPosicoes() {
        playerX = 300;
        enemyX = 390;
        playerYBase = 80;
        enemyYBase = 80;
    }

    public void inicializarSprites() {
        background = assets.backgrounds[state.nivelAtual - 1];
        playerAtual = assets.playerBase[0];
        enemyBase = assets.inimigoBaseNivel[state.nivelAtual - 1];
        enemyGolpes = assets.inimigoGolpesNivel[state.nivelAtual - 1];
        enemyAtual = enemyBase[0];
    }

    public void avancarNivel() {
        state.indiceGolpeEnemy = 0;

        if (state.nivelAtual > GameState.TOTAL_NIVEIS) {
            return;
        }

        background = assets.backgrounds[state.nivelAtual - 1];
        enemyBase = assets.inimigoBaseNivel[state.nivelAtual - 1];
        enemyGolpes = assets.inimigoGolpesNivel[state.nivelAtual - 1];

        enemyAtual = enemyBase[0];
        state.enemyLives = GameState.MAX_LIVES;
        playerAtual = assets.playerBase[0];
    }

    public boolean executarGolpeJogador() {
        state.enemyLives = Math.max(0, state.enemyLives - 1);
        assets.punchSound.play();

        playerAtual = assets.playerGolpes[state.indiceGolpePlayer];
        state.indiceGolpePlayer = (state.indiceGolpePlayer + 1) % 3;

        boolean golpeFatal = state.enemyLives <= 0;

        if (!golpeFatal) {
            enemyAtual = enemyBase[1];
        } else {
            enemyAtual = enemyBase[2];
            playerSpriteAntesDelay = playerAtual;
            enemySpriteAntesDelay = enemyAtual;
        }

        state.golpeFatal = golpeFatal;
        return golpeFatal;
    }

    public boolean executarGolpeInimigo() {
        state.playerLives = Math.max(0, state.playerLives - 1);
        assets.punchSound.play();

        enemyAtual = enemyGolpes[state.indiceGolpeEnemy];
        state.indiceGolpeEnemy = (state.indiceGolpeEnemy + 1) % 3;

        boolean golpeFatal = state.playerLives <= 0;

        if (!golpeFatal) {
            playerAtual = assets.playerBase[1];
        } else {
            playerAtual = assets.playerBase[2];
            playerSpriteAntesDelay = playerAtual;
            enemySpriteAntesDelay = enemyAtual;
        }

        state.golpeFatal = golpeFatal;
        return golpeFatal;
    }

    public void resetSpritesParaPadrao() {
        playerAtual = assets.playerBase[0];
        enemyAtual = enemyBase[0];
    }

    public void resetSpritesAposAnimacao() {
        resetSpritesParaPadrao();
    }

    public float calcularOffsetIdle(float tempoIdle, boolean isPlayer) {
        if (!state.animando && !state.emContagem && state.jogoAtivo &&
            !state.aguardandoInicio && !state.emDelayEntreFases && !state.jogoPausado) {

            float phase = isPlayer ? 0 : (float) Math.PI;
            return (float) Math.sin(tempoIdle * Math.PI * GameState.FREQUENCIA_IDLE + phase) * GameState.AMPLITUDE_IDLE;
        }
        return 0f;
    }
}
