package cloud.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * @program: comehome
 * @description:
 * @author: ErFeng_V
 * @create: 2021-05-30 19:59
 */
@Controller       // contentType="image/jpeg"       因为这里是一张验证码图片   但要在 session中保存生成的标准验证     验证码图片破解难度
public class ValCodeController {

    private static final String VALIDATECODE = "validateCode";

    @RequestMapping(value = "/verifyCodeServlet", method = RequestMethod.GET)
    public void valcode(HttpServletRequest req, HttpServletResponse resp) {
        initxuan();
        // 定义图像buffer        
        BufferedImage buffImg = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        // 创建一个随机数生成器类        
        Random random = new Random();
        // 将图像填充为白色        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 创建字体，字体的大小应该根据图片的高度来定。        
        Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
        // 设置字体。        
        g.setFont(font);
        // 画边框。        
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        // 随机产生10条干扰线，使图象中的认证码不易被其它程序探测到。
        g.setColor(Color.BLACK);
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。        
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;
        // 随机产生codeCount数字的验证码。        
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。        
            String strRand = String.valueOf(codeSequence[random.nextInt(36)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。        
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            // 用随机产生的颜色将验证码绘制到图像中。        
            g.setColor(new Color(red, green, blue));
            g.drawString(strRand, (i + 1) * x, codeY);
            // 将产生的四个随机数组合在一起。        
            randomCode.append(strRand);
        }

        // 将四位数字的验证码保存到Session中。        
        HttpSession session = req.getSession();
        session.setAttribute(VALIDATECODE, randomCode.toString());
        //允许跨域访问
        resp.setHeader("Access-Control-Allow-Origin", "*");
        // 禁止图像缓存。        
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

        resp.setContentType("image/jpeg");   //
        try {
            // 将图像输出到Servlet输出流中。
            ServletOutputStream sos = resp.getOutputStream();
            ImageIO.write(buffImg, "jpeg", sos);
            sos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 验证码图片的宽度。
    private int width = 68;
    // 验证码图片的高度。        
    private int height = 26;
    // 验证码字符个数        
    private int codeCount = 4;
    private int x = 0;
    // 字体高度        
    private int fontHeight;
    private int codeY;
    char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 初始化验证图片属性
     */
    public void initxuan() {
        // 从web.xml中获取初始信息        
        // 宽度        
        String strWidth = "68";
        // 高度        
        String strHeight = "26";
        // 字符个数        
        String strCodeCount = "4";
        // 将配置的信息转换成数值        
        try {
            width = Integer.parseInt(strWidth);
            height = Integer.parseInt(strHeight);
            codeCount = Integer.parseInt(strCodeCount);
        } catch (NumberFormatException ignored) {
        }
        x = width / (codeCount + 1);
        fontHeight = height - 2;
        codeY = height - 4;
    }

}
