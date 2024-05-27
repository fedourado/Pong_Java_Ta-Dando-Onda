

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import java.util.Locale;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.gl2.GLUT;
import textura.Textura;

/**
 *
 * @author Fernanda Dourado
 */
public class Cena implements GLEventListener, KeyListener {
	private GL2 gl;
	private GLUT glut;
	private float aspect;
                  
                  private float angulo = 0;
                  private int tonalizacao = GL2.GL_SMOOTH;
                  float luzR = 0.2f, luzG = 0.2f, luzB = 0.2f;
                  private float incAngulo = 0;
                  private float alfa = 1f;
                  
                  
	private float xTranslateBall = 0;
	private float yTranslateBall = 1f;
	private char xDirection;
	private char yDirection = 'd';
	private float livesAnimation = 0;
	private float fase2Animation = 0;

	private boolean isGamePaused = false;
	private int gameFase = 0;
	private float gameSpeed = 0.02f;

	private float userBarMove = 0;
	private int userScore = 0;
	private int userLives = 5;
        
                  // Atributos    
                  private float limite;
                  float xMin, xMax, yMin, yMax, zMin, zMax;
                  private boolean triangulo = false;

                  //Referencia para classe Textura
                  private Textura textura = null;
                  //Quantidade de Texturas a ser carregada
                  private int totalTextura = 1;

                  //Constante para carregar imagem
                  public static final String MENU = "imagens/fundo-menu.jpg";
                  public static final String BALL = "imagens/BeachBallTexture2.jpg";
                  public static final String GAMEOVER = "imagens/gameover.jpg";
                  public static final String BOARD = "imagens/wood3.jpg";
                  public static final String FASEONE = "imagens/fase1.jpg";
                  public static final String FASETWO = "imagens/fase2.jpg";
                  

                  private int filtro = GL2.GL_LINEAR; ////GL_NEAREST ou GL_LINEAR
                  private int wrap = GL2.GL_REPEAT;  //GL.GL_REPEAT ou GL.GL_CLAMP
                  private int modo = GL2.GL_MODULATE; ////GL.GL_MODULATE ou GL.GL_DECAL ou GL.GL_BLEND
                  private String texto;
                  
	//light test
	private int toning = GL2.GL_SMOOTH;
                  private boolean lightOn = true;

	@Override
	public void init(GLAutoDrawable drawable) {
		randomRunBall();
                  //dados iniciais da cena
                  GL2 gl = drawable.getGL().getGL2();

                  angulo = 0;
                  incAngulo = 0;
                  limite = 1;

                  texto = " GL_REPEAT";

                  //Cria uma instancia da Classe Textura indicando a quantidade de texturas
                  textura = new Textura(totalTextura);

                  //habilita o buffer de profundidade
                  gl.glEnable(GL2.GL_DEPTH_TEST);

	}     

	@Override
	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
                                    glut = new GLUT(); //objeto da biblioteca glut

                                    //define a cor da janela (R, G, G, alpha)
                                    gl.glClearColor(0, 0, 0, 0);

                                    //limpa o buffer de profundidade
                                    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
                                    gl.glLoadIdentity(); //lê a matriz identidade
                                   
                       
                                    
		switch (gameFase) {
		case 0:
			runMenu();
			break;
		case 1:
			runFaseOne();
			break;
		case 2:
			runFaseTwo();
			break;
		case 3:
			gameOver();
			break;
		}
		if (lightOn) {
			lithingScheme();
			turnLightOn();
        }
		if (!lightOn) {
            turnLightOff();
            
        }

 	
                                    gl.glRotatef(angulo, 0, 1, 1);

                                //não é geração de textura automática
                                    textura.setAutomatica(false);

                                    //configura os filtros
                                    textura.setFiltro(filtro);
                                    textura.setModo(GL2.GL_DECAL);
                                    textura.setWrap(wrap);  
                                   
                                    //cria a textura indicando o local da imagem e o índice
                                    textura.gerarTextura(gl,MENU, 0);

                                    gl.glColor3f(1f,1f,1f);


                                    gl.glBegin(GL2.GL_QUADS);
                                        gl.glTexCoord2f(0.0f, 0.0f);   gl.glVertex2f(-100.0f,-100.0f);
                                        gl.glTexCoord2f(0.0f, limite);  gl.glVertex2f(-100.0f,100.0f);
                                        gl.glTexCoord2f(limite, limite); gl.glVertex2f(100.0f,100.0f);
                                        gl.glTexCoord2f(limite, 0.0f);  gl.glVertex2f(100.0f,-100.0f);                
                                    gl.glEnd();
                                    

                                    //desabilita a textura indicando o índice
                                    textura.desabilitarTextura(gl, 0);

                                    gl.glPopMatrix();

                                    gl.glColor3f(1f, 1f, 1f); 

		
		gl.glFlush();
	}
	
	public void lithingScheme(){
		float[] ambientLight = { 0.7f, 0.7f, 0.7f, 1f };  
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0);  
		
        float difuseLight[] = {0.8f, 0.8f, 0.8f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, difuseLight, 0);
        
        float lightPosition[] = {-50.0f, 0.0f, 100.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
    }

    
    public void turnLightOn() {
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);    
        gl.glShadeModel(toning);
    }

    public void turnLightOff() {
        gl.glDisable(GL2.GL_LIGHT0);
        gl.glDisable(GL2.GL_LIGHTING);
    }

	public void randomRunBall() {
		double xRandom = -0.8f + Math.random() * 1.6f;
		if (xRandom > 0) {
			xDirection = 'r';
		} else {
			xDirection = 'l';
		}
		xTranslateBall = Float.valueOf(String.format(Locale.US, "%.2f", xRandom));
	}

	public void resetData() {
		xTranslateBall = 0;
		yTranslateBall = 1f;
		yDirection = 'd';

		isGamePaused = false;
		gameFase = 0;

		userBarMove = 0;
		userScore = 0;
		userLives = 5;
	}

	public void runMenu() {
		String size = "big";
		float left = -0.3f;
		float begin = 0.8f;
              
		drawText(left, begin -= 0.1f, size, "Bem-vindo(s) ao Pong Tá Dando Onda");
		drawText(left, begin -= 0.1f, size, "O objetivo do jogo é obter a mais quantidade de pontos");
		drawText(left, begin -= 0.1f, size, "rebatendo a bola e impedindo ela de cair para fora da prancha.");
		drawText(left, begin -= 0.1f, size, "-----------------------------");
		drawText(left, begin -= 0.1f, size, "# Comandos:");
		drawText(left, begin -= 0.1f, size, "- Mover a prancha = < > ou A D");
		drawText(left, begin -= 0.1f, size, "- Pausar jogo = P");
		drawText(left, begin -= 0.1f, size, "- Parar o jogo e ir para o menu inicial = X");
		drawText(left, begin -= 0.1f, size, "-----------------------------");
		drawText(left, begin -= 0.1f, size, "# Regras:");
		drawText(left, begin -= 0.1f, size, "- Cada vez que a bola for rebatida será acumalado 10 pontos");
		drawText(left, begin -= 0.1f, size, "- Quando acumular 150 pontos o jogador irá para a próxima fase");
		drawText(left, begin -= 0.1f, size, "- Na segunda fase os pontos são infinitos \\o/");
		drawText(left, begin -= 0.1f, size, "-----------------------------");
		drawText(left, begin -= 0.1f, size, "PRESSIONE 'ENTER' PARA INICIAR O JOGO");
                
                                    backgroundMenu();
	}
        
                    public void backgroundMenu() {
                        
                                gl.glEnable(GL2.GL_BLEND);
                                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);


                                textura.setAutomatica(false);
                                
                                        //configura os filtros
                                textura.setFiltro(filtro);
                                textura.setModo(GL2.GL_MODULATE);
                                textura.setWrap(wrap);  

                                //configura os filtros
                                textura.setFiltro(filtro);
                                textura.setModo(GL2.GL_DECAL);
                                textura.setWrap(wrap);  

                                //cria a textura indicando o local da imagem e o índice
                                textura.gerarTextura(gl, MENU, 0);

                                gl.glColor3f(1f,1f,1f);

                                gl.glBegin(GL2.GL_QUADS);
                                    gl.glTexCoord2f(0.0f, 0.0f);   gl.glVertex2f(-1.0f,-1.0f);
                                    gl.glTexCoord2f(0.0f, limite);  gl.glVertex2f(-1.0f,1.0f);
                                    gl.glTexCoord2f(limite, limite); gl.glVertex2f(1.0f,1.0f);
                                    gl.glTexCoord2f(limite, 0.0f);  gl.glVertex2f(1.0f,-1.0f);                
                                gl.glEnd();

                                //desabilita a textura indicando o índice
                                textura.desabilitarTextura(gl, 0);

	}
        

	public void runFaseOne() {
		if (!isGamePaused) {
			ballPhysicsEngine();
		} else {
			drawText(-0.2f, 0, "big", "JOGO PAUSADO");
		}

		drawBottomBar();
		drawBall();

		if (userScore == 30) {
			gameFase = 2;
		}

		if (userLives == 0) {
			gameFase = 3;
		}
    
		drawText(0.8f, 0.9f, "big", "Pontuação: " + userScore);
                                    backgroundOne();

		for (int i = 1; i <= 5; i++) {
			if (userLives >= i)
				drawLives(0.1f * i, true);
			else
				drawLives(0.1f * i, false);
		}
	}
	
                public void backgroundOne() {
                    
                      textura.setAutomatica(false);
                
                    //configura os filtros
                    textura.setFiltro(filtro);
                    textura.setModo(GL2.GL_DECAL);
                    textura.setWrap(wrap);  

                    //cria a textura indicando o local da imagem e o índice
                    textura.gerarTextura(gl, FASEONE, 0);

                    gl.glColor3f(1f,1f,1f);

                    gl.glBegin(GL2.GL_QUADS);
                        gl.glTexCoord2f(0.0f, 0.0f);   gl.glVertex2f(-1.0f,-1.0f);
                        gl.glTexCoord2f(0.0f, limite);  gl.glVertex2f(-1.0f,1.0f);
                        gl.glTexCoord2f(limite, limite); gl.glVertex2f(1.0f,1.0f);
                        gl.glTexCoord2f(limite, 0.0f);  gl.glVertex2f(1.0f,-1.0f);                
                    gl.glEnd();

                    //desabilita a textura indicando o índice
                    textura.desabilitarTextura(gl, 0);
                    
	}
                  
        
        
	public void drawFase2Art() {
		drawText(-0.13f, 0.6f, "big", "FASE 2 - ONDA SINISTRA");
                                    backgroundTwo();
	}
	
         public void backgroundTwo() {
                    
                      textura.setAutomatica(false);
                
                    //configura os filtros
                    textura.setFiltro(filtro);
                    textura.setModo(GL2.GL_DECAL);
                    textura.setWrap(wrap);  

                    //cria a textura indicando o local da imagem e o índice
                    textura.gerarTextura(gl, FASETWO, 0);

                    gl.glColor3f(1f,1f,1f);

                    gl.glBegin(GL2.GL_QUADS);
                        gl.glTexCoord2f(0.0f, 0.0f);   gl.glVertex2f(-1.0f,-1.0f);
                        gl.glTexCoord2f(0.0f, limite);  gl.glVertex2f(-1.0f,1.0f);
                        gl.glTexCoord2f(limite, limite); gl.glVertex2f(1.0f,1.0f);
                        gl.glTexCoord2f(limite, 0.0f);  gl.glVertex2f(1.0f,-1.0f);                
                    gl.glEnd();

                    //desabilita a textura indicando o índice
                    textura.desabilitarTextura(gl, 0);
                    
	}
	

	public void runFaseTwo() {
		gameSpeed = 0.03f;
		if (!isGamePaused) {
			ballPhysicsEngine();
		} else {
			drawText(-0.2f, 0, "big", "JOGO PAUSADO");
		}

		drawBottomBar();
		drawBall();
		drawObstableFase2();
		drawFase2Art();

		if (userLives == 0) {
			gameFase = 3;
		}

		drawText(0.8f, 0.9f, "big", "Pontuação: " + userScore);

		for (int i = 1; i <= 5; i++) {
			if (userLives >= i)
				drawLives(0.1f * i, true);
			else
				drawLives(0.1f * i, false);
		}
	}

                     public void drawObstableFase2() {
		gl.glPushMatrix();
			gl.glBegin(GL2.GL_QUADS);				
				gl.glColor3f(1, 1, 1);
				gl.glVertex2f(-0.2f, -0.2f);
				gl.glVertex2f(-0.2f, -0.5f);
				gl.glVertex2f(-0.2f, -0.2f);
				gl.glVertex2f(-0.7f, -0.7f);
			gl.glEnd();
		gl.glPopMatrix();
	}
                     
                         
	public void drawBottomBar() {
            
                                 textura.setAutomatica(false);

                                  //configura os filtros
                                  textura.setFiltro(filtro);
                                  textura.setModo(GL2.GL_DECAL);
                                  textura.setWrap(wrap);  

                                  //cria a textura indicando o local da imagem e o índice
                                  textura.gerarTextura(gl, BOARD, 0);

                                  gl.glColor3f(1f,1f,1f);
                                  
                                    gl.glPushMatrix();
			gl.glTranslatef(userBarMove, 0, 0);
			gl.glBegin(GL2.GL_QUADS);
				gl.glColor3f(0.0f, 0.0f, 0.0f);
				gl.glVertex2f(-0.2f, -0.8f);
				gl.glColor3f(1.0f, 0.0f, 0.0f);
				gl.glVertex2f(0.2f, -0.8f);
				gl.glColor3f(0.0f, 1.0f, 0.0f);
				gl.glVertex2f(0.2f, -0.9f);
				gl.glColor3f(0.0f, 0.0f, 1.0f);
				gl.glVertex2f(-0.2f, -0.9f);
			gl.glEnd();
		gl.glPopMatrix();
    
                  }	

        
	public void drawLives(float pos, boolean filled) {
		gl.glPushMatrix();
		if (filled)
			gl.glColor3f(1, 0.2f, 0);
		else
			gl.glColor3f(1, 1, 1);

		gl.glTranslatef(0.4f + pos, 0.8f, 0);
		gl.glRotatef(livesAnimation, 1, 1, 0);
		livesAnimation += 0.8f;

		glut.glutSolidTeapot(0.03f);
		gl.glPopMatrix();
	}

	public void gameOver() {          
		float begin = 0.8f;
		float left = -0.1f;
		drawText(left, begin -= 0.1f, "big", " -----------");
		drawText(left, begin -= 0.1f, "big", "| GAME OVER |");
		drawText(left, begin -= 0.1f, "big", " -----------");
		drawText(left, begin -= 0.1f, "big", "Pontuação final: " + userScore);
		drawText(left, begin -= 0.1f, "big", "E - Menu inicial");
		drawText(left, begin -= 0.1f, "big", "F - Fechar o jogo");
                                    gameOverBack();
	}
        
                    public void gameOverBack() {
                        
                              gl.glEnable(GL2.GL_BLEND);
                              gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

                              textura.setAutomatica(false);

                              //configura os filtros
                              textura.setFiltro(filtro);
                              textura.setModo(GL2.GL_DECAL);
                              textura.setWrap(wrap);  

                              //cria a textura indicando o local da imagem e o índice
                              textura.gerarTextura(gl, GAMEOVER, 0);

                              gl.glColor3f(1f,1f,1f);

                              gl.glBegin(GL2.GL_QUADS);
                                  gl.glTexCoord2f(0.0f, 0.0f);   gl.glVertex2f(-1.0f,-1.0f);
                                  gl.glTexCoord2f(0.0f, limite);  gl.glVertex2f(-1.0f,1.0f);
                                  gl.glTexCoord2f(limite, limite); gl.glVertex2f(1.0f,1.0f);
                                  gl.glTexCoord2f(limite, 0.0f);  gl.glVertex2f(1.0f,-1.0f);                
                              gl.glEnd();

                              //desabilita a textura indicando o índice
                              textura.desabilitarTextura(gl, 0);

                  }
                    
   
        

	public void drawText(float x, float y, String size, String phrase) {
		gl.glRasterPos2f(x, y);
		switch (size) {
			case "small":
				glut.glutBitmapString(GLUT.BITMAP_8_BY_13, phrase);
				break;
			case "big":
				glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, phrase);
		}
	}

	public boolean isBallInRangeOfBar(float xTranslatedBallFixed) {
		float leftBarLimit = Float.valueOf(String.format(Locale.US, "%.1f", userBarMove - 0.2f));
		float rightBarLimit = Float.valueOf(String.format(Locale.US, "%.1f", userBarMove + 0.2f));

		if (leftBarLimit <= xTranslatedBallFixed && rightBarLimit >= xTranslatedBallFixed) {
			return true;
		}

		return false;
	}

	public boolean isObjectInYRange(float xObj, float yObj, float bLimit, float tLimit, float xPoint) {
		if (tLimit >= yObj && bLimit <= yObj && xObj == xPoint) {
			return true;
		}

		return false;
	}

	public boolean isObjectInXRange(float xObj, float heightObj, float lLimit, float rLimit, float tLimit) {
		if (lLimit <= xObj && rLimit >= xObj && heightObj == tLimit) {
			return true;
		}

		return false;
	}

	public void ballPhysicsEngine() {
		float xTransBallFixed = Float.valueOf(String.format(Locale.US, "%.1f", xTranslateBall));
		float yTransBallFixed = Float.valueOf(String.format(Locale.US, "%.1f", yTranslateBall));

		if (gameFase == 2 && xDirection == 'l'
				&& isObjectInYRange(xTransBallFixed, yTransBallFixed, -0.1f, 0.5f, 0.2f)) {
			xDirection = 'r';
		}
		if (gameFase == 2 && xDirection == 'r'
				&& isObjectInYRange(xTransBallFixed, yTransBallFixed, -0.1f, 0.5f, -0.2f)) {
			xDirection = 'l';
		} else if (xTransBallFixed > -1f && xDirection == 'l') {
			xTranslateBall -= gameSpeed/2;
		} else if (xTransBallFixed == -1f && xDirection == 'l') {
			xDirection = 'r';
		} else if (xTransBallFixed < 1f && xDirection == 'r') {
			xTranslateBall += gameSpeed/2;
		} else if (xTransBallFixed == 1f && xDirection == 'r') {
			xDirection = 'l';
		}

		if (gameFase == 2 && yDirection == 'u'
				&& isObjectInXRange(xTransBallFixed, yTransBallFixed, -0.2f, 0.2f, -0.2f)) {
			yDirection = 'd';
		} else if (gameFase == 2 && yDirection == 'd'
				&& isObjectInXRange(xTransBallFixed, yTransBallFixed, -0.2f, 0.2f, 0.6f)) {
			yDirection = 'u';
		} else if (yTransBallFixed == -0.7f && yDirection == 'd' 
				&& isBallInRangeOfBar(xTransBallFixed)) {
			yDirection = 'u';
			lightOn = false;
			toning = toning == GL2.GL_SMOOTH ? GL2.GL_FLAT : GL2.GL_SMOOTH;
			userScore += 10;
		} else if (yTransBallFixed < 0.9f && yDirection == 'u') {
			yTranslateBall += gameSpeed;
		} else if (yTransBallFixed == 0.9f && yDirection == 'u') {
			yDirection = 'd';
		} else if (yTransBallFixed < -1f) {
			yTranslateBall = 1f;
			xTranslateBall = 0;
			userLives--;
			randomRunBall();
		} else {
			yTranslateBall -= gameSpeed;
			lightOn = true;
			toning = toning == GL2.GL_SMOOTH ? GL2.GL_FLAT : GL2.GL_SMOOTH;
		}
	}

	

	public void drawBall() {
 
                                    ballColor();
		gl.glPushMatrix();
		gl.glTranslatef(xTranslateBall, yTranslateBall, 0);
		gl.glColor3f(1, 1, 1);
  
                                    gl.glColor3f(1f,1f,1f);

		double limit = 2 * Math.PI;
		double i;
		double cX = 0;
		double cY = 0;
		double rX = 0.1f / aspect;
		double rY = 0.1f;
                
		gl.glBegin(GL2.GL_POLYGON);
		for (i = 0; i < limit; i += 0.01) {
			gl.glVertex2d(cX + rX * Math.cos(i), cY + rY * Math.sin(i));
  
		}                   
		gl.glEnd();  
		gl.glPopMatrix();
	}
        
        
                  public void ballColor() {
                                                 
                                    
                                    textura.setAutomatica(false);

                                    //configura os filtros
                                    textura.setFiltro(filtro);
                                    textura.setModo(GL2.GL_DECAL);
                                    textura.setWrap(wrap);  

                                    gl.glEnable(GL2.GL_TEXTURE_2D);
                                    //cria a textura indicando o local da imagem e o índice
                                    textura.gerarTextura(gl, BALL, 0);
                                    
                                    //desabilita a textura indicando o índice
                                    gl.glDisable(GL2.GL_TEXTURE_2D);
                
}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		aspect = (float) width / height;
		gl.glOrtho(-1, 1, -1, 1, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		case KeyEvent.VK_A:
			if (userBarMove > -0.8 && !isGamePaused) {
				userBarMove = userBarMove - 0.1f;
			}
			break;
		case KeyEvent.VK_LEFT:
			if (userBarMove > -0.8 && !isGamePaused) {
				userBarMove = userBarMove - 0.1f;
			}
			break;
		case KeyEvent.VK_D:
			if (userBarMove < 0.8 && !isGamePaused) {
				userBarMove = userBarMove + 0.1f;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if (userBarMove < 0.8 && !isGamePaused) {
				userBarMove = userBarMove + 0.1f;
			}
			break;
		case KeyEvent.VK_P:
			isGamePaused = !isGamePaused;
			break;
		case KeyEvent.VK_ENTER:
			if (gameFase == 0) {
				gameFase = 1;
			}
			break;
		case KeyEvent.VK_X:
			if (gameFase > 0) {
				gameFase = 0;
				resetData();
			}
			break;
		case KeyEvent.VK_E:
			if (gameFase == 3) {
				gameFase = 0;
				resetData();
			}
			break;
		case KeyEvent.VK_F:
			if (gameFase == 3) {
				System.exit(0);
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
