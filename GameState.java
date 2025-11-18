package br.mackenzie;

import java.util.ArrayList;
import java.util.List;

public class GameState {
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
    public static final float DURACAO_MOSTRAR_PONTUACAO = 3f;
    public static final float DURACAO_TELA_FIM = 3f;

    public static final float[] LIMITES_TEMPO = {7f, 6f, 5f};
    public static final int[] METAS_PEDALADAS = {20, 25, 30};

    public int playerLives = MAX_LIVES;
    public int enemyLives = MAX_LIVES;
    public int nivelAtual = 1;
    public boolean jogoAtivo = true;
    public boolean jogadorVenceu = false;
    public boolean emTransicao = false;
    public float tempoTransicao = 0f;

    public int pedaladas;
    public float tempo;
    public float limiteTempo;
    public int metaPedaladas;

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

    public float tempoAnimacao = 0f;
    public boolean animando = false;
    public int indiceGolpePlayer = 0;
    public int indiceGolpeEnemy = 0;
    public float tempoIdle = 0f;

    public boolean golpeFatal = false;

    public float pontuacaoAtual = 0f;
    public float[] pontuacoesNivel = new float[TOTAL_NIVEIS];
    public List<Float> pontuacoesRoundAtual = new ArrayList<Float>();
    public boolean mostrandoPontuacao = false;
    public float tempoMostrarPontuacao = 0f;
    public boolean mostrandoPontuacaoFinal = false;
    public boolean aguardandoTelaFim = false;

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
        aguardandoTelaFim = false;
        resetPontuacoes();
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

    public void calcularPontuacaoRound(int pedaladas, float tempo) {
        if (tempo <= 0) {
            pontuacoesRoundAtual.add(0.0f);
            return;
        }

        float clicksPorSegundo = pedaladas / tempo;
        float pontuacaoRound = Math.min(clicksPorSegundo / 2f, 3f);

        pontuacoesRoundAtual.add(clicksPorSegundo);

        if (pontuacoesNivel[nivelAtual - 1] == 0) {
            pontuacoesNivel[nivelAtual - 1] = pontuacaoRound;
        } else {
            pontuacoesNivel[nivelAtual - 1] =
                (pontuacoesNivel[nivelAtual - 1] + pontuacaoRound) / 2f;
        }

        pontuacaoAtual = pontuacoesNivel[nivelAtual - 1];
    }

    public List<Float> getPontuacoesRoundCompletas() {
        List<Float> completas = new ArrayList<Float>();

        for (Float pontuacao : pontuacoesRoundAtual) {
            completas.add(pontuacao);
        }

        while (completas.size() < 5) {
            completas.add(0.0f);
        }

        return completas;
    }

    public void reiniciarPontuacoesNivel() {
        pontuacaoAtual = 0f;
        pontuacoesRoundAtual.clear();
    }

    public int getEstrelas() {
        if (pontuacaoAtual >= 2.5f) return 3;
        if (pontuacaoAtual >= 1.5f) return 2;
        if (pontuacaoAtual >= 0.8f) return 1;
        return 0;
    }

    public void resetPontuacoes() {
        pontuacaoAtual = 0f;
        pontuacoesNivel = new float[TOTAL_NIVEIS];
        pontuacoesRoundAtual.clear();
        mostrandoPontuacao = false;
        tempoMostrarPontuacao = 0f;
        mostrandoPontuacaoFinal = false;
        aguardandoTelaFim = false;
    }
}
