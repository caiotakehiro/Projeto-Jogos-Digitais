package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;

public class CharacterManager {
    private AssetManager assets;
    private GameState state;

    // Posições
    public float playerX, playerYBase;
    public float enemyX, enemyYBase;

    // Sprites atuais
    public Texture playerAtual;
    public Texture enemyAtual;
    public Texture background;

    // Sprites para delay
    public Texture playerSpriteAntesDelay;
    public Texture enemySpriteAntesDelay;

    // Arrays atuais de sprites do inimigo
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
        playerAtual = assets.playerBase[0]; // player_1.png
        enemyBase = assets.inimigoBaseNivel[state.nivelAtual - 1];
        enemyGolpes = assets.inimigoGolpesNivel[state.nivelAtual - 1];
        enemyAtual = enemyBase[0]; // enemy1_1.png
    }

    public void avancarNivel() {
        state.indiceGolpeEnemy = 0;
        state.nivelAtual++;

        if (state.nivelAtual > GameState.TOTAL_NIVEIS) {
            state.jogoAtivo = false;
            state.jogadorVenceu = true;
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

        // Verificar se foi golpe fatal
        boolean golpeFatal = state.enemyLives <= 0;

        if (!golpeFatal) {
            // Golpe normal 
            enemyAtual = enemyBase[1];
        } else {
            // Golpe fatal 
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

        // Verificar se foi golpe fatal
        boolean golpeFatal = state.playerLives <= 0;

        if (!golpeFatal) {
            // Golpe normal 
            playerAtual = assets.playerBase[1];
        } else {
            // Golpe fatal 
            playerAtual = assets.playerBase[2];
            playerSpriteAntesDelay = playerAtual;
            enemySpriteAntesDelay = enemyAtual;
        }

        state.golpeFatal = golpeFatal;
        return golpeFatal;
    }

    public void resetSpritesParaPadrao() {
        // Volta para padrão 
        playerAtual = assets.playerBase[0]; // player_1.png
        enemyAtual = enemyBase[0]; // enemy1_1.png
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
