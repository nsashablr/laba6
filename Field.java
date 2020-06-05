package lab6;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Field extends JPanel {

    // Флаг приостановленности движения
    private boolean paused;


    // Динамический список скачущих мячей
    private ArrayList<BouncingBall> balls = new ArrayList<BouncingBall>(10);
    public ArrayList<BouncingBall> getBalls() { return balls; }
    public void setBalls(ArrayList<BouncingBall> balls) { this.balls = balls; }


    // Размеры матрицы
    private static  int arrayH=600;
    private static  int arrayW=600;
    public static int getArrayH() {
        return arrayH;
    }
    public static int getArrayW() {
        return arrayW;
    }

    // Класс таймер отвечает за регулярную генерацию событий ActionEvent
    // При создании его экземпляра используется анонимный класс, реализующий интерфейс ActionListener
    private Timer repaintTimer = new Timer(10, new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            // Задача обработчика события ActionEvent - перерисовка окна
            repaint();
        }
    });


    // Конструктор класса
    public Field() {
        // Установить цвет заднего фона
        setBackground(Color.GRAY);
        // Запустить таймер
        repaintTimer.start();
    }

    // Унаследованный от JPanel метод перерисовки компонента
    public void paintComponent(Graphics g) {
        // Вызвать версию метода, унаследованную от предка
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
        // Последовательно запросить прорисовку от всех мячей из списка
        for (BouncingBall ball : balls) {
            ball.paint(canvas);
        }
    }
    // Метод добавления нового мяча в список
    public void addBall() {
        // Заключается в добавлении в список нового экземпляра BouncingBall
        // Всю инициализацию положения, скорости, размера, цвета
        // BouncingBall выполняет сам в конструкторе
        balls.add(new BouncingBall(this));

    }

    // Пауза
    public void pause() {
        // Включить режим паузы;
        paused = true;
    }
    
    // Метод синхронизированный, т.е. только один поток может одновременно быть внутри
    public synchronized void resume() {
        // Выключить режим паузы
        paused = false;
        
        // Будим все ожидающие продолжения потоки
        notifyAll();
    }
    // Режим ускорения
    public synchronized void acceleration() {
        for(BouncingBall ball: balls)
        {
            ball.setSpeedX(ball.getSpeedX() * 1.2);
            ball.setSpeedY(ball.getSpeedY() * 1.2);
            ball.setX(ball.getX() + ball.getSpeedX());
            ball.setY(ball.getY() + ball.getSpeedY());
        }
    }
   // Режим замедления
   public synchronized void retard() {
        for(BouncingBall ball: balls)
        {
            ball.setSpeedX(ball.getSpeedX() * 0.8);
            ball.setSpeedY(ball.getSpeedY() * 0.8);
            ball.setX(ball.getX() + ball.getSpeedX());
            ball.setY(ball.getY() + ball.getSpeedY());
        }
    }
    // Синхронизированный метод проверки, может ли мяч двигаться (не включен ли режим паузы?)
    public synchronized void canMove(BouncingBall ball) throws InterruptedException {
        // Если режим паузы включен, то поток, зашедший внутрь данного метода, засыпает
        if (paused) {
            wait();
        }
        
    }
}