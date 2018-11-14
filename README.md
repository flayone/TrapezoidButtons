# TrapezoidButtons
一款Android双梯形自定义按钮控件，通过配置参数可以修改显示效果，可以变形为矩形，控制描边显示，自定义按钮字体。
比较有挑战的是斜边描边的防锯齿效果，以及如何做到斜边和其他边一样宽。

我是通过两个不同大小颜色的path重叠效果实现的描边效果，虽然官方有Paint.Style.STROKE方法设置描边，但很不好用。
原因一个是Paint.Style.STROKE方法即使设置了防锯齿，依然会在反复点击刷新view的时候出现很明显的锯齿效果，
而且Paint.Style.STROKE方法显示的描边各个边并不是同等粗细，视觉效果很差。

效果预览

<img src="images/img.jpg" width="45%" />