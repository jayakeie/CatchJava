import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BlockGame extends JFrame {

	// 수정 확인을 위한 주석처리
	//변수 선언 구간
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_SIZE = 20;
    private static final int BRICK_WIDTH = 50;
    private static final int BRICK_HEIGHT = 20;
    private static final int BRICKS_ROW = 7;
    private static final int BRICKS_COL = 5;
    private static final int NUM_BRICKS = 35;
    //추가된 변수
    static int BLOCK_GAP = 5; 

    private int paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
    private int ballX = WIDTH / 2 - BALL_SIZE / 2;
    private int ballY = HEIGHT / 2 - BALL_SIZE / 2;
    private int ballSpeedX = 3;
    private int ballSpeedY = -3;

    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;

    private boolean[] bricks;

    private static final int MAX_BALLS = 3;
    private int remainingBalls = MAX_BALLS;
    
    private JButton restartButton;

    public BlockGame() {
        setTitle("Block Breaker Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftKeyPressed = true;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                	rightKeyPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftKeyPressed = false;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rightKeyPressed = false;
                }
            }
        });

        restartButton = new JButton("재시작"); //재시작 버튼 생성
        restartButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 - 15, 100, 30);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restart();
            }
        });
        restartButton.setVisible(false);
        add(restartButton);
        
        bricks = new boolean[NUM_BRICKS];
        for (int i = 0; i < NUM_BRICKS; i++) {
            bricks[i] = true;
        }

        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                repaint();
            }
        });
        timer.start();
    }
    
    private void restart() { //RESTART 버튼을 눌렀을 때 
        remainingBalls = MAX_BALLS;
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballSpeedX = 3;
        ballSpeedY = -3;

        for (int i = 0; i < NUM_BRICKS; i++) {
            bricks[i] = true;
        }

        restartButton.setVisible(false);
    }

    
    private void update() {
        if (leftKeyPressed && paddleX > 0) {
            paddleX -= 5;
        }
        if (rightKeyPressed && paddleX < WIDTH - PADDLE_WIDTH) {
            paddleX += 5;
        }

        ballX += ballSpeedX;
        ballY += ballSpeedY;

        if (ballX <= 0 || ballX >= WIDTH - BALL_SIZE) {
            ballSpeedX = -ballSpeedX;
        }
        if (ballY <= 0) {
            ballSpeedY = -ballSpeedY;
        }

        if (ballY >= HEIGHT) {
        	// 볼이 아래로 떨어져서 플레이 횟수 감소
            remainingBalls--;
            // 남은 플레이 횟수가 있으면 게임을 초기화하고 다시 시작
            if (remainingBalls > 0) {
            	//화면 아래로 벗어난 경우, 공의 위치를 화면 정중앙으로 재설정
            	//ball의 X 좌표를 width 값의 반 - ball 사이즈 값의 반으로 지정함
            	//Y 좌표 또한 동일하게 수정
                ballX = WIDTH / 2 - BALL_SIZE / 2;
                ballY = HEIGHT / 2 - BALL_SIZE / 2;
                ballSpeedX = 3;
                ballSpeedY = -3;
            } else {
                // 남은 플레이 횟수가 없으면 재시작 버튼
            	showGameOverDialog();
            }
        }
        //볼이 바에 닿을 때만 반전되도록 수정
        if (ballY >= HEIGHT - BALL_SIZE - PADDLE_HEIGHT && ballX + BALL_SIZE >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
            ballSpeedY = -ballSpeedY; // 볼이 바에 닿았을 때만 반전
            ballY = HEIGHT - BALL_SIZE - PADDLE_HEIGHT; // 볼이 바에 닿은 후 미끄러지지 않도록 위치를 조정
        }

        for (int i = 0; i < NUM_BRICKS; i++) {
            int brickX = i % BRICKS_ROW * (BRICK_WIDTH + BLOCK_GAP) + 10; // 수정된 부분
            int brickY = i / BRICKS_ROW * (BRICK_HEIGHT + BLOCK_GAP) + 50; // 50 블록이 시작하는 Y좌표
        	if (bricks[i] && ballX + BALL_SIZE >= i * BRICK_WIDTH && ballX <= (i + 1) * BRICK_WIDTH && ballY + BALL_SIZE >= 0 && ballY <= BRICK_HEIGHT) {
                ballSpeedY = -ballSpeedY;
                bricks[i] = false;
            }
        }
    }
    
    private void showGameOverDialog() {
        // 사용자 정의 다이얼로그 생성
        JDialog dialog = new JDialog(this, "게임 오버", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JLabel messageLabel = new JLabel("게임이 종료되었습니다.");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        dialog.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton restartButton = new JButton("RESTART");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restart();
                dialog.dispose(); // 다이얼로그 닫기
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(restartButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.fillRect(paddleX, HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        for (int i = 0; i < BRICKS_COL; i++) {
        	for (int j=0; j< BRICKS_ROW; j++) {
        		int brickIndex = i * BRICKS_ROW + j;  // 1차원 배열을 2차원 배열로
        		if (bricks[brickIndex]) {
                    // Draw bricks
        			int brickX = j * (BRICK_WIDTH + BLOCK_GAP) + 10;
        			int brickY = i * (BRICK_HEIGHT + BLOCK_GAP) + 50;  // 50 블록이 시작하는 Y좌표
        			g.fillRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
        		}	
        	}
        }

        // Display remaining balls
        g.setColor(Color.RED);
        g.drawString("Balls: " + remainingBalls, 10, 20);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BlockGame().setVisible(true);
            }
        });
    }
}
