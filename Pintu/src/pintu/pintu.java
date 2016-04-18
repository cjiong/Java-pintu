package pintu;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class pintu extends Applet implements MouseListener,KeyListener,Runnable
{
	//存放每个拼图块的图片数组
	Image[] m_Image = new Image[9];
	//未分割的大图片
	Image m_ImageAll;
	//标志当前各个拼图块的排列状况
	int m_nImage[][] = new int[3][3]; 
	//表示空白拼图块
	final int NO_IMAGE = -1;
	//每个拼图块的宽和高
	final int IMAGE_WIDTH = 120;
	final int IMAGE_HEIGHT = 120;
	
	//各个移动方向
	final int DIRECTION_UP = 1;
    final int DIRECTION_DOWN = 2;
    final int DIRECTION_LEFT = 3;
    final int DIRECTION_RIGHT = 4;
    static int DIRECTION_NONE = -1;
     
    //信息提示区域的宽度
    final int TIPSAREA_WIDTH = 120;
    //标志游戏是否结束或是否重新开始游戏
    boolean startNewGame = false;
    //步数
    int stepNumber = 0;
    //游戏时间
    int gameTimer = 0;
    //计时器线程
    Thread theTimer;
    
    //图片个数
    int numberOfImage = 3;
    //预览原图的开关
    boolean showAllImage = false;
    
    //初始化
    public void init()
    {
    	//设置小程序的窗口大小
    	this.setSize(480, 360);
    	//添加媒体监视器
    	MediaTracker mediaTracker = new MediaTracker(this);
    	//装载图片
    	m_ImageAll = getImage(getDocumentBase(),"pic/Image1.jpg");
    	
    	//添加图片到监视器列表，分配id给这个图片
    	mediaTracker.addImage(m_ImageAll, 1);
    	
    	try
    	{
    		//加载图片
    		mediaTracker.waitForID(1);
    	}
    	catch(Exception e)
    	{
    		System.out.println("图片加载失败");
    	}
    	if(mediaTracker.isErrorAny()) 
    		System.out.println("图片加载失败");
    	
    	//将一个大图分割成九个小图
    	for(int i = 0; i < 9; i++)
    	{
    		m_Image[i] = createImage(IMAGE_WIDTH, IMAGE_HEIGHT);
    		Graphics g = m_Image[i].getGraphics();
    		int row = i % 3;
    		int col = i / 3;
    		g.drawImage(m_ImageAll, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, row * IMAGE_WIDTH, col * IMAGE_HEIGHT, (row + 1)*IMAGE_WIDTH, (col + 1)*IMAGE_HEIGHT, this);
    	}
    	//初始化完成
    	System.out.println("初始化完成");
    	//为线程分配内存空间
    	theTimer = new Thread(this);
    	//开始线程
    	theTimer.start();
    	initData();
    	//添加鼠标事件监听器和键盘事件监听器
    	addMouseListener(this);
    	addKeyListener(this);
     	
    } 
    //实现计时功能
    @Override
    public void run()
    	{
    	 	while(Thread.currentThread() == theTimer)
    		{
    		   try
    		   {
    			   //使当前线程休眠
    			   Thread.sleep(1000);
    			   String status = "You've been playing for " + gameTimer + " seconds.";
    			   if(gameTimer > 200) status = status + "Come on!";
    			   else status = status + "Take it easy.";
    			   showStatus(status);
    			   if(!startNewGame)  gameTimer++;
    		   }
    		   catch(Exception e)
    		   {
    		   }
    		}
    	}
    	
    //绘制图片 
    public void paint(Graphics g)
    	{
    	    //填充左边的信息提示区域，颜色为蓝色
    		g.setColor(Color.blue);
    		g.fillRect(0, 0, TIPSAREA_WIDTH, IMAGE_HEIGHT * 3);
    		//设置字体以及绘制提示信息
    		g.setFont(new Font("宋体", Font.PLAIN, 15));
    		g.setColor(Color.red);
    		g.drawString("步数：" + stepNumber, 5, 20);
    		g.drawString("现有图片" + numberOfImage + "张", 5, 60);
    		g.drawString("请按1-3改变图片", 5, 100);
   
    		g.setColor(Color.blue);
    		//预览原图
    		if(showAllImage)
    		{
    			int x = TIPSAREA_WIDTH;
    			int y = 0;
    			g.drawImage(m_ImageAll, x, y, this);
    			return;
    		}
    		//绘制图片区域
    		for(int i = 0; i < 3; i++)
    		{
    			for(int j = 0; j < 3; j++)
    			{
    				int x=i * IMAGE_WIDTH + TIPSAREA_WIDTH;
    				int y=j * IMAGE_HEIGHT;
    				
    				if(m_nImage[i][j] == NO_IMAGE)
    					g.fill3DRect(x, y, IMAGE_WIDTH, IMAGE_HEIGHT, false);
    				
    				else
    				{
    					g.drawImage(m_Image[m_nImage[i][j]], x, y, this);
    					g.drawRect(x, y, IMAGE_WIDTH, IMAGE_HEIGHT);
    				}
    			}
    		}
    		checkStatus();
    		if(startNewGame)
    		{
    			//绘制提示信息
    			g.setColor(Color.blue);
    			g.drawString("请按任意键重新开始", 5, 140);
    			g.setColor(Color.red);
    			g.setFont(new Font("宋体", Font.PLAIN, 40));
    			g.drawString("你完成了游戏", 70 + TIPSAREA_WIDTH,160);
    			g.drawString("祝贺你！",110 + TIPSAREA_WIDTH, 210);
    		}
    	}
    	
     	
    
    //判断拼图是否摆放正确
    public void checkStatus()
    {
    	//定义成员变量，默认值为真
    	boolean win = true;
    	int correctNumber = 0;
    	for(int j = 0; j < 3; j++)
    	{
    		for(int i = 0; i < 3; i++)
    		{
    			if(m_nImage[i][j] != correctNumber && m_nImage[i][j] != NO_IMAGE)
    				win = false;
    			correctNumber++;
    		}
    	}
    	//
    	if(win) 
    		startNewGame = true;
    }
    
    
    //将拼图块的顺序打乱
    public void initData()
	{
    	//给每个小图配一个数字标识
		int[] hasDistrib = new int[9];
		for(int i = 0; i < 9; i++) 
			hasDistrib[i] = 0;
		//将9张小图摆放到任意位置
		for(int j = 0; j < 3; j++)
		{
			for(int i = 0; i < 3; i++)
			{
				int imageNumber = 0;
				do
				{
					imageNumber = (int)(Math.random() * 9);
				}while(hasDistrib[imageNumber] == 1);
				
				m_nImage[i][j] = imageNumber;
				hasDistrib[imageNumber] = 1;
			}
		}
		//随机选取一个拼图块为空拼图块
		m_nImage[(int)(Math.random() * 3)][(int)(Math.random() * 3)] = NO_IMAGE;
		//清空步数和计时器
		stepNumber = 0;
		gameTimer = 0;
	}
    
     
    //鼠标事件
    public void mouseClicked(MouseEvent e)
    {
    	//
    	if(showAllImage) return;
    	if(startNewGame)
    	{
    		initData();
    		repaint();
    		startNewGame = false;
    		return;
    	}
    	//获取鼠标点击的坐标，根据坐标求出所点击的拼图块
    	int X = e.getX() - TIPSAREA_WIDTH;
    	int Y = e.getY();
    	int col = Y / IMAGE_HEIGHT;
    	int row = X / IMAGE_WIDTH;
    	System.out.println("colomn:" + col + "  row:" + row);
    	int direction=directionCanMove(col, row);
    	if(direction!=DIRECTION_NONE)
    	{
    		move(col, row, direction);
    		stepNumber++;
    		repaint();
    	}
    	else
    	{
    		
    	}
    }
    
    public void mouseEntered(MouseEvent e)
    {
       // TODO Auto-generated method stub
    }
    
    public void mouseExited(MouseEvent e)
    {
    	// TODO Auto-generated method stub
    }
    
    public void mousePressed(MouseEvent e)
    {
    	// TODO Auto-generated method stub
    }
    
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
    //检查哪个方向可以移动
    public int directionCanMove(int col,int row)
    //
    {
    	if((col-1) >= 0)
    		if(m_nImage[row][col - 1] == NO_IMAGE)
    			return DIRECTION_UP;
    	
    	if((col+1) <= 2)
    		if(m_nImage[row][col + 1] == NO_IMAGE)
    			return DIRECTION_DOWN;
    	
    	if((row-1) >= 0)
    		if(m_nImage[row - 1][col] == NO_IMAGE)
    			return DIRECTION_LEFT;
    	
    	if((row+1) <= 2)
    		if(m_nImage[row + 1][col] == NO_IMAGE)
    			return DIRECTION_RIGHT;
    	
    	return DIRECTION_NONE;
    }
    //实现移动拼图块
    public void move(int col,int row,int direction)
    {
    	switch(direction)
    	{
    	case DIRECTION_UP:
    		m_nImage[row][col - 1] = m_nImage[row][col];
    		m_nImage[row][col] = NO_IMAGE;
    		break;
    	case DIRECTION_DOWN:
    		m_nImage[row][col + 1] = m_nImage[row][col];
    		m_nImage[row][col] = NO_IMAGE;
    		break;
    	case DIRECTION_LEFT:
    		m_nImage[row - 1][col] = m_nImage[row][col];
    		m_nImage[row][col] = NO_IMAGE;
    		break;
    	case DIRECTION_RIGHT:
    		m_nImage[row + 1][col] = m_nImage[row][col];
    		m_nImage[row][col] = NO_IMAGE;
    		break;
    	}
    }
    
    //键盘事件
    public void keyPressed(KeyEvent e)
    {
    	System.out.println("press key "+ KeyEvent.getKeyText(e.getKeyCode()));
    	int direction = DIRECTION_NONE;
    	switch(e.getKeyCode())
    	{
    	case KeyEvent.VK_DOWN:
    		direction = DIRECTION_DOWN;
    		break;
    	case KeyEvent.VK_UP:
    		direction = DIRECTION_UP;
    		break;
    	case KeyEvent.VK_LEFT:
    		direction = DIRECTION_LEFT;
    		break;
    	case KeyEvent.VK_RIGHT:
    		direction = DIRECTION_RIGHT;
    		break;
    	//点击键盘上的R，游戏重新开始
    	case KeyEvent.VK_R:
    		initData(); 
    		repaint();
    		return;
    	//切换图片
    	case KeyEvent.VK_1:
    	case KeyEvent.VK_2:
    	case KeyEvent.VK_3:
    		int numberOfCurrentImage = e.getKeyCode() - KeyEvent.VK_1 + 1;
    		if(numberOfCurrentImage <= numberOfImage)
    		{
    			System.out.println(numberOfCurrentImage);
    			changeImage(numberOfCurrentImage);
    			initData();
    			repaint();
    		}
    		break;
    	//预览全图
    	case KeyEvent.VK_Y:
    		if(showAllImage)
    			showAllImage = false;
    		else
    			showAllImage = true;
    		repaint();
    		return;
    	default:
    		return;
    	}
    	boolean canMove = move(direction);
    	if(canMove)
    	{
    		stepNumber++;   		
    		repaint();
    	}
    	else
    	{
    	}
    }
    
    public void keyReleased(KeyEvent e)
    {
    	// TODO Auto-generated method stub
    }
    
    public void keyTyped(KeyEvent e)
    {
    	// TODO Auto-generated method stub
    }
    //实现切换图片功能
    public void changeImage(int imageNumber)
     {
    	if(imageNumber > numberOfImage)
    	{
    		showStatus("你要的图片不存在！！");
    		return;
    	}
    	//创建一个监视器
    	MediaTracker mediaTracker = new MediaTracker(this);
    	//加载所需图片
    	m_ImageAll=getImage(getDocumentBase(), "pic/Image" + imageNumber+ ".jpg");
    	//将图片添加到监视器列表
    	mediaTracker.addImage(m_ImageAll, 1);
    	try
    	{
    		mediaTracker.waitForAll();
    	}
    	catch(Exception e)
    	{
    		System.out.println("图片加载失败");
    	}
    	
    	for(int i = 0; i < 9; i++)
    	{
    		m_Image[i] = createImage(IMAGE_WIDTH, IMAGE_HEIGHT);
    		Graphics g = m_Image[i].getGraphics();
    		int row = i % 3;
    		int col = i / 3;
    		g.drawImage(m_ImageAll, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, row*IMAGE_WIDTH, col*IMAGE_HEIGHT, (row+1)*IMAGE_WIDTH, (col+1)*IMAGE_HEIGHT, this);
    	}
    }
    //拼图块是否能移动，参数是方向
    public boolean move(int direction)
    {
    	int imageCol = -1;
    	int imageRow = -1;
    	int i = 0;
    	int j = 0;
    	while (i < 3 && imageRow == -1)
    	{
    		while (j < 3 && imageCol == -1)
    		{
    			if(m_nImage[i][j] == NO_IMAGE)
    			{
    				imageRow = i;
    				imageCol = j;
    			}
    			j++;
    		}
    		j=0;
    		i++;
    	}
    	//
    	//
    	switch(direction)
    	{
    	case DIRECTION_UP:
    		if(imageCol == 3)return false;
    		m_nImage[imageRow][imageCol] = m_nImage[imageRow][imageCol + 1];
    		m_nImage[imageRow][imageCol + 1] = NO_IMAGE;
    		break;
    	case DIRECTION_DOWN:
    		if(imageCol == 0)return false;
    		m_nImage[imageRow][imageCol] = m_nImage[imageRow][imageCol - 1];
    		m_nImage[imageRow][imageCol - 1] = NO_IMAGE;
    		break; 
    	case DIRECTION_LEFT:
    		if(imageRow == 3)return false;
    		m_nImage[imageRow][imageCol] = m_nImage[imageRow + 1][imageCol];
    		m_nImage[imageRow + 1][imageCol] = NO_IMAGE;
    		break;
    	case DIRECTION_RIGHT:
    		if(imageRow == 0)return false;
    		m_nImage[imageRow][imageCol] = m_nImage[imageRow - 1][imageCol];
    		m_nImage[imageRow - 1][imageCol] = NO_IMAGE;
    		break;
    	}
    	return true;
    }

}