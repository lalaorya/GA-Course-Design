package sample;

import java.lang.String;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.soap.Text;
import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.ResourceBundle;


public class Controller {

    /**
     * 注册组件
     */
    @FXML
    //绑定过程文本域
    private TextArea textArea1;
    //绑定结果文本域
    @FXML
    private TextArea textArea2;
    //绑定折线图
    @FXML
    private LineChart chart;
    //绑定运行按钮
    @FXML
    private Button run;
    //绑定表格
    @FXML
    private TableColumn id;
    @FXML
    private TableColumn weight;
    @FXML
    private TableColumn value;
    @FXML
    private TableView allTable;

    //绑定参数文本框
    @FXML
    private TextField capacityField;
    @FXML
    private TextField scaleField;
    @FXML
    private TextField maxGenField;
    @FXML
    private TextField pcField;
    @FXML
    private TextField pmField;

    @FXML
    private SplitPane splitPane;

    private  ObservableList<GoodsBean> data = FXCollections.observableArrayList();

    //初始化滑动窗口位置
    public void initialize(){
        splitPane.setDividerPositions(0.20,0.78);
    }

    //按钮样式事件方法
    public void buttonEnterStyle(){
        run.setBorder(new Border(new BorderStroke(Paint.valueOf("#0ff"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(3))));
    }
    public void buttonExitStyle(){
        run.setBorder(new Border(new BorderStroke(Paint.valueOf("#ffff"), BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
    }


    public void moverun(MouseEvent event) {
        run();
    }
    /**
     * 读取文件数据添加到表格
     * @throws IOException
     */
    public void readFile() throws IOException {
        //新建一个文件选择窗口
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            System.out.println(file.getAbsolutePath());

            //封装到表格
            BufferedReader br=new BufferedReader(new FileReader(file));
            String[] wArray=br.readLine().trim().split("[A-Za-z：]+| |,");
            for (String s:wArray) {
                System.out.println(s);
            }
            String[] vArray=br.readLine().trim().split("[A-Za-z：]+| |,");
            if(wArray.length!=vArray.length) System.out.println("数据错误");

            //设置列可编辑
            allTable.setEditable(true);
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            weight.setCellValueFactory(new PropertyValueFactory<>("weight"));
            weight.setCellFactory(TextFieldTableCell.forTableColumn());
            value.setCellValueFactory(new PropertyValueFactory<>("value"));
            value.setCellFactory(TextFieldTableCell.forTableColumn());

            //添加数据到表格
            int index=0;
            for(int i=1;i<wArray.length;i++){
//                if(wArray[i].equals('\n'))  continue;
                data.addAll(new GoodsBean((i-1)+"",wArray[i],vArray[i]));
            }

            allTable.setItems(data);
        }

    }

    /**
     * 运行按钮事件方法
     */
    public void run(){
        //获取表格的数据
        ObservableList<GoodsBean> list=allTable.getItems();
        //处理数据
        double[] w=new double[list.size()];
        double[] v=new double[list.size()];
        int i=0;
        for(GoodsBean goodsBean:list){
            w[i]=Double.parseDouble(goodsBean.getWeight());
            v[i]=Double.parseDouble(goodsBean.getValue());
            System.out.println(goodsBean.toString());
            i++;
        }
        //获取参数
        double capacity=Double.parseDouble(capacityField.getText());
        int scale=Integer.parseInt(scaleField.getText());
        int maxGen=Integer.parseInt(maxGenField.getText());
        double pc=Double.parseDouble(pcField.getText());
        double pm=Double.parseDouble(pmField.getText());

        //调用GA算法
        GA_knapsack ga=new GA_knapsack(w,v,capacity,scale,maxGen,pc,pm,textArea1,textArea2,chart);
        ga.solution();
    }

    //清空表格数据
    public void clearTable(){
        allTable.getItems().removeAll(data);
    }

    //清空控制台1
    public void clearConsole1(){
        textArea1.setText("");
    }

    //清空控制台2
    public void clearConsole2(){
        textArea2.setText("");
        chart.getData().setAll((Collection) null);
    }

    // 清空参数
    public void clearPara(){
        capacityField.setText("");
        scaleField.setText("");
        maxGenField.setText("");
        pcField.setText("");
        pmField.setText("");
    }

    //关于作者 事件方法
    public void aboutAuthor() {
        // 创建新的stage
        Stage secondStage = new Stage();
        Label label = new Label(""); // 放一个标签
        TextArea textArea=new TextArea();
        textArea.setText("  18李四    ");
        textArea.appendText("qq:1111111\n");
        textArea.appendText("  18张三");
        textArea.setEditable(false);

        textArea.setMinSize(600,400);
        StackPane secondPane = new StackPane(label);
        secondPane.getChildren().add(textArea);
        Scene secondScene = new Scene(secondPane, 600, 400);
        secondStage.setScene(secondScene);
        secondStage.setTitle("关于作者");
        secondStage.show();
    }

    //用户帮助 事件方法
    public void userHelp() {
        // 创建新的stage
        Stage secondStage = new Stage();
        Label label = new Label(""); // 放一个标签
        TextArea textArea=new TextArea();
        textArea.appendText("使用步骤：\n");
        textArea.appendText("  ①点击File菜单栏--导入数据\n  ②右侧设置遗传参数\n  ③点击运行按钮即可查看结果\n\n");
        textArea.appendText("文件格式：\n");
        textArea.appendText("  weight：0 2 4 8...(空格分隔)\n");
        textArea.appendText("  value：1 3 5 9...(空格分隔)\n");
        textArea.setEditable(false);
        textArea.setMinSize(600,400);
        StackPane secondPane = new StackPane(label);
        secondPane.getChildren().add(textArea);
        Scene secondScene = new Scene(secondPane, 600, 400);
        secondStage.setScene(secondScene);
        secondStage.setTitle("使用手册");
        secondStage.show();
    }
}
