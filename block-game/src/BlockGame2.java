package testwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class testdes extends JFrame {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_SIZE = 20;
    private static final int BRICK_WIDTH = 50;
    private static final int BRICK_HEIGHT = 20;
    private static final int NUM_BRICKS = 40;

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

    public testdes() {
        setTitle("블록 깨기 게임");
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

        bricks = new boolean[NUM_BRICKS];
        for (int i = 0; i < NUM_BRICKS; i++) {
            bricks[i] = true;
        }

        restartButton = new JButton("재시작"); //재시작 버튼 생성
        restartButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 - 15, 100, 30);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                게임다시시작();
            }
        });
        restartButton.setVisible(false);
        add(restartButton);

        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                repaint();
            }
        });
        timer.start();
    }

    private void 게임다시시작() { //RESTART 버튼을 눌렀을 때 
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
            remainingBalls--;
            if (remainingBalls > 0) {
                ballX = WIDTH / 2 - BALL_SIZE / 2;
                ballY = HEIGHT / 2 - BALL_SIZE / 2;
                ballSpeedX = 3;
                ballSpeedY = -3;
            } else {
                showGameOverDialog();
            }
        }

        if (ballY >= HEIGHT - BALL_SIZE - PADDLE_HEIGHT && ballX + BALL_SIZE >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
            ballSpeedY = -Math.abs(ballSpeedY);
            ballY = HEIGHT - BALL_SIZE - PADDLE_HEIGHT;
        }

        for (int i = 0; i < NUM_BRICKS; i++) {
            if (bricks[i] && ballX + BALL_SIZE >= i * BRICK_WIDTH && ballX <= (i + 1) * BRICK_WIDTH && ballY + BALL_SIZE >= 0 && ballY <= BRICK_HEIGHT) {
                ballSpeedY = -ballSpeedY;
                bricks[i] = false;
            }
            if (remainingBalls <= 0) {
                restartButton.setVisible(true);
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
                게임다시시작();
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

        for (int i = 0; i < NUM_BRICKS; i++) {
            if (bricks[i]) {
                g.fillRect(i * BRICK_WIDTH, 0, BRICK_WIDTH, BRICK_HEIGHT);
            }
        }

        g.setColor(Color.RED);
        g.drawString("남은 공: " + remainingBalls, 10, 20);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new testdes().setVisible(true);
            }
        });
    }
}