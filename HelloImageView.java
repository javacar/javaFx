import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class HelloImageView extends Application {
    private Stage stage;
    private HBox box;
    private File file;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        box = new HBox();
        BorderPane bp = new BorderPane();
        Scene scene = new Scene(bp);
        Button button = new Button("打开图片");
        Button _button = new Button("一寸转两寸");
        FlowPane fp = new FlowPane(button, _button);
        bp.setCenter(box);
        bp.setTop(fp);
        stage.setX(0);
        stage.setY(0);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        button.setOnAction(this::open);
        _button.setOnAction(this::changeSize);
    }

    private void changeSize(ActionEvent event) {
        if (file == null) return;
        ImageView _imageView = new ImageView();
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.getDialogPane().setPrefWidth(1000);
        inputDialog.showAndWait();
        String pathname = inputDialog.getEditor().getText();
        Image image2 = new Image(pathname);
        try {
            reSize(file.getPath(), pathname, 350, 490, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image2 = new Image(pathname);
        _imageView.setImage(image2);
        _imageView.setFitWidth(image2.getWidth());
        _imageView.setFitHeight(image2.getHeight());
        _imageView.setPreserveRatio(true);
        _imageView.setSmooth(true);
        _imageView.setCache(true);
        box.getChildren().add(_imageView);
        stage.setWidth(1000);
        stage.setHeight(1000);
    }

    private void open(ActionEvent event) {
        File file = new FileChooser().showOpenDialog(stage);
        if (file == null || !file.isFile()) {
            return;
        }
        ImageView imageView = new ImageView();
        Image image = new Image(file.getPath());
        imageView.setImage(image);
        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        box.getChildren().add(imageView);
        stage.setWidth(1000);
        stage.setHeight(1000);
        this.file = file;
    }


    /**
     *  图片尺寸放大
     */
    public void reSize(String srcPathImg, String destPathImg, int width,
                       int height, boolean equalScale) throws IOException {
        File srcImg = new File(srcPathImg);
        File destImg = new File(destPathImg);
        String type = getImageType(srcImg);
        if (type == null) {
            return;
        }
        if (width < 0 || height < 0) {
            return;
        }

        BufferedImage srcImage = ImageIO.read(srcImg);
        if (srcImage != null) {
            double sx = (double) width / srcImage.getWidth();
            double sy = (double) height / srcImage.getHeight();
            // 等比缩放
            if (equalScale) {
                if (sx > sy) {
                    sx = sy;
                    width = (int) (sx * srcImage.getWidth());
                } else {
                    sy = sx;
                    height = (int) (sy * srcImage.getHeight());
                }
            }
            ColorModel cm = srcImage.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            BufferedImage target = new BufferedImage(cm, raster, alphaPremultiplied, null);
            Graphics2D g = target.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawRenderedImage(srcImage, AffineTransform.getScaleInstance(sx, sy));
            g.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(target, type, baos);
            FileOutputStream fos = new FileOutputStream(destImg);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        }
    }

    private static String getImageType(File file) {
        if (file != null && file.exists() && file.isFile()) {
            String fileName = file.getName();
            int index = fileName.lastIndexOf(".");
            if (index != -1 && index < fileName.length() - 1) {
                return fileName.substring(index + 1);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch();
    }
}
   
