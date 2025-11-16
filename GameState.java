package br.mackenzie;

public class GameState {
    // Constantes do jogo
    public static final int MAX_LIVES = 3;
    public static final int TOTAL_NIVEIS = 3;
    public static final float DURACAO_ANIMACAO = 0.5f;
    public static final float AMPLITUDE_IDLE = 3f;
    public static final float FREQUENCIA_IDLE = 2f;
    public static final float DURACAO_CONTAGEM = 3f;
    public static final float DELAY_NORMAL = 0.8f;
    public static final float DELAY_FATAL = 1.2f;
    public static final float DELAY_ENTRE_FASES = 1.5f;
    public static final float DURACAO_TRANSICAO = 0.5f;

    // Dificuldade por nível
    public static final float[] LIMITES_TEMPO = {7f, 6f, 5f};
    public static final int[] METAS_PEDALADAS = {20, 25, 30};

    // Estado do jogo
    public int playerLives = MAX_LIVES;
    public int enemyLives = MAX_LIVES;
    public int nivelAtual = 1;
    public boolean jogoAtivo = true;
    public boolean jogadorVenceu = false;
    public boolean emTransicao = false;
    public float tempoTransicao = 0f;

    // Mecânica principal
    public int pedaladas;
    public float tempo;
    public float limiteTempo;
    public int metaPedaladas;

    // Estados
    public boolean emContagem = true;
    public float tempoContagem = 0f;
    public boolean aguardandoProximoRound = false;
    public float tempoAguardar = 0f;
    public float delayAtual = DELAY_NORMAL;
    public boolean emDelayEntreFases = false;
    public float tempoDelayEntreFases = 0f;
    public boolean aguardandoInicio = true;
    public boolean jogoPausado = false;
    public boolean menuAtivo = false;

    // Animação
    public float tempoAnimacao = 0f;
    public boolean animando = false;
    public int indiceGolpePlayer = 0;
    public int indiceGolpeEnemy = 0;
    public float tempoIdle = 0f;

    // Controle de golpes
    public boolean golpeFatal = false;

    public void atualizarDificuldade() {
        limiteTempo = LIMITES_TEMPO[nivelAtual - 1];
        metaPedaladas = METAS_PEDALADAS[nivelAtual - 1];
    }

    public void resetRodada() {
        pedaladas = 0;
        tempo = 0;
        animando = false;
        tempoAnimacao = 0;
        aguardandoProximoRound = false;
        tempoAguardar = 0f;
        golpeFatal = false;
        delayAtual = DELAY_NORMAL;
    }

    public void reiniciarJogo() {
        nivelAtual = 1;
        playerLives = MAX_LIVES;
        enemyLives = MAX_LIVES;
        jogoAtivo = true;
        jogoPausado = false;
        menuAtivo = false;
        aguardandoInicio = true;
        emContagem = false;
        emDelayEntreFases = false;
        indiceGolpePlayer = 0;
        indiceGolpeEnemy = 0;
        golpeFatal = false;
        atualizarDificuldade();
        resetRodada();
    }

    public boolean verificarGolpeFatal(boolean golpeDoJogador) {
        if (golpeDoJogador) {
            return enemyLives <= 0;
        } else {
            return playerLives <= 0;
        }
    }
}
