# Java-pintu

拼图游戏是一个简单的Applet小程序，游戏规则为将一张大图片分割成9张小图，并将其顺序打乱，如何随机挑选出其中的8张小图，摆放入3x3的矩阵中的随机位置。通过鼠标点击或者键盘移动打乱的8张小图，使其复原成原先的图片，如果8块小图都在正确的位置，则游戏成功。
     按“R”重新开始；按“Y”预览全图；按“1、2、3”切换图片。

拼图游戏是继承自Applet类的小程序，并引入awt绘图包，绘制界面和图片，实现Runnable接口和用Thread类来实现计时的功能，实现 MouseListener和KeyListener接口和引入事件处理机制，实现对鼠标事件和键盘事件的监听，以实现移动拼图块，切换图片，预览全图等功能。

![](https://github.com/cjiong/Java-pintu/raw/master/audi.jpg)

![](https://github.com/cjiong/Java-pintu/raw/master/audi1.jpg)
