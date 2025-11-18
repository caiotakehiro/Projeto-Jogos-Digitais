package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private OrthographicCamera camera;

    private GameState state;
    private AssetManager assets;
    private CharacterManager characters;

    @Override
    public void create() {
        inicializarSistema();

        state = new GameState();
        assets = new AssetManager();
        characters = new CharacterManager(assets, state);

        assets.carregarTexturas();
        assets.carregarAudio();
        inicializarJogo();
    }

    private void inicializarSistema() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        spriteBatch = new SpriteBatch();
    }

    private void inicializarJogo() {
        characters.inicializarSprites();
        state.atualizarDificuldade();
        state.aguardandoInicio = true;
        state.resetRodada();

        if (!assets.backgroundMusic.isPlaying()) {
            assets.backgroundMusic.play();
        }
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        processarInputTelaFinal();

        if (state.mostrandoPontuacao || state.aguardandoTelaFim) {
            return;
        }

        processarInputMenu();
        if (state.menuAtivo) return;
        processarInputJogo();
    }

    private void processarInputTelaFinal() {
        if (!state.jogoAtivo && state.aguardandoTelaFim) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
                reiniciarJogo();
            }
        }
    }

    private void processarInputMenu() {
        if (state.jogoAtivo && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            toggleMenu();
            return;
        }

        if (state.menuAtivo && Gdx.input.justTouched()) {
            processarCliqueMenu();
        }
    }

    private void toggleMenu() {
        if (!state.menuAtivo) {
            state.menuAtivo = true;
            state.jogoPausado = true;
            assets.backgroundMusic.pause();
        } else {
            state.menuAtivo = false;
            state.jogoPausado = false;
            assets.backgroundMusic.play();
        }
    }

    private void processarCliqueMenu() {
        Vector3 coords = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float worldX = coords.x;
        float worldY = coords.y;

        float buttonWidth = 157;
        float buttonHeight = 48;
        float centerX = viewport.getWorldWidth() / 2 - buttonWidth / 2;
        float playY = viewport.getWorldHeight() / 2 - 10;
        float restartY = viewport.getWorldHeight() / 2 - 80;

        if (clicouNoBotao(worldX, worldY, centerX, playY, buttonWidth, buttonHeight)) {
            state.menuAtivo = false;
            state.jogoPausado = false;
            assets.backgroundMusic.play();
        }

        if (clicouNoBotao(worldX, worldY, centerX, restartY, buttonWidth, buttonHeight)) {
            reiniciarJogo();
        }
    }

    private boolean clicouNoBotao(float worldX, float worldY, float botaoX, float botaoY, float largura, float altura) {
        return worldX >= botaoX && worldX <= botaoX + largura &&
            worldY >= botaoY && worldY <= botaoY + altura;
    }

    private void processarInputJogo() {
        if (state.mostrandoPontuacao || state.aguardandoTelaFim || state.emTransicao || state.emDelayEntreFases) {
            return;
        }

        if (state.aguardandoInicio && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            iniciarRound();
            return;
        }

        if (podePedalar() && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            state.pedaladas++;
            if (state.pedaladas >= state.metaPedaladas) {
                finalizarRound(true);
            }
        }
    }

    private boolean podePedalar() {
        return !state.aguardandoInicio &&
            !state.emContagem &&
            state.jogoAtivo &&
            !state.aguardandoProximoRound &&
            !state.emDelayEntreFases &&
            !state.jogoPausado &&
            !state.mostrandoPontuacao &&
            !state.aguardandoTelaFim &&
            !state.emTransicao;
    }

    private void iniciarRound() {
        state.aguardandoInicio = false;
        state.emContagem = true;
        state.tempoContagem = 0f;
    }

    private void logic() {
        if (!state.jogoAtivo || state.jogoPausado) return;

        if (state.mostrandoPontuacao) {
            processarMostrarPontuacao();
            return;
        }

        if (state.emTransicao) {
            processarTransicao();
            return;
        }

        if (state.emDelayEntreFases) {
            processarDelayEntreFases();
            return;
        }

        if (state.aguardandoInicio) return;

        state.tempoIdle += Gdx.graphics.getDeltaTime();

        if (state.aguardandoProximoRound) {
            processarAguardandoProximoRound();
            return;
        }

        if (state.animando) {
            processarAnimacao();
        }

        if (state.emContagem) {
            processarContagem();
            return;
        }

        processarTempoRound();
    }

    private void processarMostrarPontuacao() {
        state.tempoMostrarPontuacao += Gdx.graphics.getDeltaTime();
        if (state.tempoMostrarPontuacao >= GameState.DURACAO_MOSTRAR_PONTUACAO) {
            state.mostrandoPontuacao = false;
            state.tempoMostrarPontuacao = 0f;

            if (state.nivelAtual >= GameState.TOTAL_NIVEIS && state.enemyLives <= 0) {
                state.jogoAtivo = false;
                state.jogadorVenceu = true;
                state.aguardandoTelaFim = true;
            } else if (state.playerLives <= 0) {
                state.jogoAtivo = false;
                state.jogadorVenceu = false;
                state.aguardandoTelaFim = true;
            } else {
                state.emTransicao = true;
                state.tempoTransicao = 0f;
            }
        }
    }

    private void processarDelayEntreFases() {
        state.tempoDelayEntreFases += Gdx.graphics.getDeltaTime();
        if (state.tempoDelayEntreFases >= GameState.DELAY_ENTRE_FASES) {
            state.emDelayEntreFases = false;
            state.tempoDelayEntreFases = 0f;
            avancarNivelAposDelay();
        }
    }

    private void processarAguardandoProximoRound() {
        state.tempoAguardar += Gdx.graphics.getDeltaTime();
        if (state.tempoAguardar >= state.delayAtual) {
            state.aguardandoProximoRound = false;
            state.tempoAguardar = 0f;

            if (state.golpeFatal) {
                state.emDelayEntreFases = true;
                state.tempoDelayEntreFases = 0f;
            } else {
                characters.resetSpritesParaPadrao();
                state.emContagem = true;
                state.tempoContagem = 0f;
                state.resetRodada();
            }
        }
    }

    private void processarAnimacao() {
        state.tempoAnimacao += Gdx.graphics.getDeltaTime();
        if (state.tempoAnimacao >= GameState.DURACAO_ANIMACAO) {
            state.animando = false;

            if (!state.golpeFatal) {
                characters.resetSpritesParaPadrao();
            }
        }
    }

    private void processarContagem() {
        state.tempoContagem += Gdx.graphics.getDeltaTime();
        if (state.tempoContagem >= GameState.DURACAO_CONTAGEM) {
            state.emContagem = false;
            state.tempoContagem = 0f;
            characters.resetSpritesParaPadrao();
            state.resetRodada();
        }
    }

    private void processarTempoRound() {
        state.tempo += Gdx.graphics.getDeltaTime();
        if (state.tempo >= state.limiteTempo && state.pedaladas < state.metaPedaladas) {
            finalizarRound(false);
        }
    }

    private void finalizarRound(boolean jogadorAcertou) {
        if (!state.jogoAtivo) return;

        state.animando = true;
        state.tempoAnimacao = 0f;

        boolean golpeFatal;
        if (jogadorAcertou) {
            golpeFatal = characters.executarGolpeJogador();
            state.calcularPontuacaoRound(state.pedaladas, state.tempo);
        } else {
            golpeFatal = characters.executarGolpeInimigo();
            state.calcularPontuacaoRound(state.pedaladas, state.tempo);
        }

        if (golpeFatal) {
            state.delayAtual = GameState.DELAY_FATAL;
        } else {
            state.delayAtual = GameState.DELAY_NORMAL;
        }

        state.aguardandoProximoRound = true;
        state.tempoAguardar = 0f;
    }

    private void avancarNivelAposDelay() {
        if (state.enemyLives <= 0) {
            state.mostrandoPontuacao = true;
            state.tempoMostrarPontuacao = 0f;

            if (state.nivelAtual >= GameState.TOTAL_NIVEIS) {
                assets.backgroundMusic.stop();
            }
        } else {
            state.mostrandoPontuacao = true;
            state.tempoMostrarPontuacao = 0f;
            assets.backgroundMusic.stop();
        }
    }

    private void reiniciarJogo() {
        state.reiniciarJogo();
        state.resetPontuacoes();
        state.mostrandoPontuacaoFinal = false;
        state.aguardandoTelaFim = false;
        characters.inicializarSprites();

        if (!assets.backgroundMusic.isPlaying()) {
            assets.backgroundMusic.play();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        if (state.emTransicao) {
            spriteBatch.draw(assets.telaPretaImg, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            spriteBatch.end();
            return;
        }

        spriteBatch.draw(characters.background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        if (state.menuAtivo) {
            desenharMenu();
            spriteBatch.end();
            return;
        }

        desenharJogo();

        if (state.mostrandoPontuacao) {
            desenharPontuacao();
        }

        spriteBatch.end();
    }

    private void processarTransicao() {
        state.tempoTransicao += Gdx.graphics.getDeltaTime();
        if (state.tempoTransicao >= GameState.DURACAO_TRANSICAO) {
            state.emTransicao = false;
            state.tempoTransicao = 0f;

            iniciarProximoNivelAposTransicao();
        }
    }

    private void iniciarProximoNivelAposTransicao() {
        state.nivelAtual++;

        if (state.nivelAtual > GameState.TOTAL_NIVEIS) {
            state.jogoAtivo = false;
            state.jogadorVenceu = true;
            assets.backgroundMusic.stop();
            return;
        }

        characters.avancarNivel();
        state.atualizarDificuldade();
        state.aguardandoInicio = true;
        state.resetRodada();
        state.reiniciarPontuacoesNivel();

        if (!assets.backgroundMusic.isPlaying()) {
            assets.backgroundMusic.play();
        }
    }

    private void desenharMenu() {
        float panelWidth = 300;
        float panelHeight = 250;
        float panelX = (viewport.getWorldWidth() - panelWidth) / 2;
        float panelY = (viewport.getWorldHeight() - panelHeight) / 2 - 40;

        spriteBatch.draw(assets.fundoMenuImg, panelX, panelY, panelWidth, panelHeight);

        float topPanelWidth = 300;
        float topPanelHeight = 80;
        float topPanelX = (viewport.getWorldWidth() - topPanelWidth) / 2;
        float topPanelY = panelY + panelHeight - 10;

        spriteBatch.draw(assets.cimaMenuImg, topPanelX, topPanelY, topPanelWidth, topPanelHeight);

        float menuTitleWidth = 157;
        float menuTitleHeight = 48;
        float menuTitleX = viewport.getWorldWidth() / 2 - menuTitleWidth / 2;
        float menuTitleY = topPanelY + (topPanelHeight - menuTitleHeight) / 2;

        spriteBatch.draw(assets.menuImg, menuTitleX, menuTitleY, menuTitleWidth, menuTitleHeight);

        float buttonWidth = 157;
        float buttonHeight = 48;
        float centerX = viewport.getWorldWidth() / 2 - buttonWidth / 2;
        float playY = viewport.getWorldHeight() / 2 - 10;
        float restartY = viewport.getWorldHeight() / 2 - 80;

        spriteBatch.draw(assets.playImg, centerX, playY, buttonWidth, buttonHeight);
        spriteBatch.draw(assets.restartImg, centerX, restartY, buttonWidth, buttonHeight);
    }

    private void desenharJogo() {
        desenharPersonagens();
        desenharUI();
    }

    private void desenharPersonagens() {
        float offsetPlayerY = characters.calcularOffsetIdle(state.tempoIdle, true);
        float offsetEnemyY = characters.calcularOffsetIdle(state.tempoIdle, false);

        Texture pDes = state.emDelayEntreFases ? characters.playerSpriteAntesDelay : characters.playerAtual;
        Texture eDes = state.emDelayEntreFases ? characters.enemySpriteAntesDelay : characters.enemyAtual;

        spriteBatch.draw(pDes, characters.playerX, characters.playerYBase + offsetPlayerY);
        spriteBatch.draw(eDes, characters.enemyX, characters.enemyYBase + offsetEnemyY);
    }

    private void desenharUI() {
        desenharVidas();

        if (state.aguardandoInicio && assets.pressSpaceImg != null) {
            desenharPressSpace();
        }

        if (!state.jogoAtivo && state.aguardandoTelaFim) {
            desenharTelaFim();
            return;
        }

        if (state.emContagem && !state.aguardandoInicio && !state.emDelayEntreFases && !state.jogoPausado) {
            desenharContagem();
        }

        if (!state.emContagem && state.jogoAtivo && !state.aguardandoInicio &&
            !state.emDelayEntreFases && !state.jogoPausado) {
            desenharBarraPedaladas();
        }
    }

    private void desenharVidas() {
        float heartSize = 32, spacing = 5;

        for (int i = 0; i < GameState.MAX_LIVES; i++) {
            float x = 20 + (heartSize + spacing) * i;
            float y = viewport.getWorldHeight() - heartSize - 20;
            Texture t = (i < state.playerLives) ? assets.heartFull : assets.heartEmpty;
            spriteBatch.draw(t, x, y, heartSize, heartSize);
        }

        for (int i = 0; i < GameState.MAX_LIVES; i++) {
            float x = viewport.getWorldWidth() - ((heartSize + spacing) * (i + 1)) - 20;
            float y = viewport.getWorldHeight() - heartSize - 20;
            Texture t = (i < state.enemyLives) ? assets.heartFull : assets.heartEmpty;
            spriteBatch.draw(t, x, y, heartSize, heartSize);
        }
    }

    private void desenharPressSpace() {
        float tx = 300, ty = 150;
        float x = (viewport.getWorldWidth() - tx) / 2;
        float y = viewport.getWorldHeight() - ty - 60;
        spriteBatch.draw(assets.pressSpaceImg, x, y, tx, ty);
    }

    private void desenharTelaFim() {
        Texture fim = state.jogadorVenceu ? assets.venceuImg : assets.perdeuImg;
        if (fim != null) {
            float tx = 300, ty = 150;
            float x = (viewport.getWorldWidth() - tx) / 2;
            float y = viewport.getWorldHeight() - ty - 60;
            spriteBatch.draw(fim, x, y, tx, ty);
        }
    }

    private void desenharContagem() {
        Texture img = null;
        if (state.tempoContagem < 1f) img = assets.count3;
        else if (state.tempoContagem < 2f) img = assets.count2;
        else if (state.tempoContagem < 3f) img = assets.count1;

        if (img != null) {
            float t = 120;
            float x = (viewport.getWorldWidth() - t) / 2;
            float y = viewport.getWorldHeight() - t - 50;
            spriteBatch.draw(img, x, y, t, t);
        }
    }

    private void desenharBarraPedaladas() {
        int estagio = calcularEstagioBarra(state.pedaladas);
        Texture barra = assets.barras[estagio - 1];
        float tx = 200, ty = 40;
        float x = (viewport.getWorldWidth() - tx) / 2;
        float y = viewport.getWorldHeight() - ty - 60;
        spriteBatch.draw(barra, x, y, tx, ty);
    }

    private int calcularEstagioBarra(int pedaladas) {
        if (pedaladas >= state.metaPedaladas) return 7;
        float proporcao = (float) pedaladas / state.metaPedaladas;
        int estagio = (int) Math.ceil(proporcao * 6);
        return Math.min(Math.max(estagio, 1), 6);
    }

    private void desenharPontuacao() {
        spriteBatch.setColor(0, 0, 0, 0.7f);
        spriteBatch.draw(assets.telaPretaImg, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.setColor(Color.WHITE);

        int estrelas = state.getEstrelas();

        float centerX = viewport.getWorldWidth() / 2;
        float estrelasY = viewport.getWorldHeight() / 2 + 40;

        desenharEstrelas(centerX, estrelasY, estrelas);

        if (!state.pontuacoesRoundAtual.isEmpty()) {
            desenharMediasRounds(centerX, estrelasY - 80);
        }
    }

    private void desenharMediasRounds(float centerX, float startY) {
        float numeroWidth = 20f;
        float numeroHeight = 30f;
        float spacing = 15f;

        java.util.List<Float> pontuacoesCompletas = state.getPontuacoesRoundCompletas();
        int totalRounds = 5;

        for (int i = 0; i < totalRounds; i++) {
            float media = pontuacoesCompletas.get(i);

            float roundX = centerX - ((totalRounds - 1) * (numeroWidth * 4 + spacing)) / 2;
            roundX += i * (numeroWidth * 4 + spacing);

            String mediaStr = String.format("%.1f", media);
            float textWidth = mediaStr.length() * numeroWidth;
            float numberX = roundX - textWidth / 2 + numeroWidth * 0.5f;
            float numberY = startY;

            desenharNumero(mediaStr, numberX, numberY, numeroWidth, numeroHeight);
        }
    }

    private void desenharNumero(String numero, float x, float y, float width, float height) {
        for (int i = 0; i < numero.length(); i++) {
            char c = numero.charAt(i);
            Texture tex = null;

            if (c >= '0' && c <= '9') {
                tex = assets.numerosPontuacao[c - '0'];
            } else if (c == '.' && assets.pontoDecimal != null) {
                tex = assets.pontoDecimal;
            }

            if (tex != null) {
                spriteBatch.draw(tex, x + i * width, y, width, height);
            }
        }
    }

    private void desenharEstrelas(float x, float y, int estrelasCheias) {
        float starSize = 50f;
        float spacing = 10f;
        float totalWidth = 3 * starSize + 2 * spacing;
        float startX = x - totalWidth / 2;

        for (int i = 0; i < 3; i++) {
            Texture star = (i < estrelasCheias) ? assets.starFull : assets.starEmpty;
            spriteBatch.draw(star, startX + i * (starSize + spacing), y, starSize, starSize);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        spriteBatch.dispose();
        assets.dispose();
    }
}
