package sample;


import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.Random;

public class GA_knapsack {
    private double[] weight;
    private double[] value;
    private double capacity;

    private int bestGen;//最佳个体的代数
    private double maxValue;//最佳个体背包的价值
    private int[] bestIndividual;//最佳个体的编码

    //定义GA相关参数
    private int chromoLen;//染色体长度，即基因个数
    private int scale;//种群规模
    private int maxGen;//最大代数
    private double pc;//交叉概率
    private double pm;//变异概率

    private int[][] initPopulation;//初始种群
    private int[][] newPopulation;//子代种群
    private double[] fitness;//当前种群各个个体的适应度
    private double[] pi;// 种群中各个个体的累计概率，用于轮盘赌选择
    private int curGen;//当前代数

    private Random random;//随机数产生器

    //文本域显示信息
    private TextArea textArea1;
    private TextArea textArea2;
    private LineChart chart;

    XYChart.Series<Number,Number> vLine;
    XYChart.Data<Number, Number> vLineData1;
    int index=0;

    public GA_knapsack(double[] weight, double[] value, double capacity, int scale, int maxGen, double pc, double pm, TextArea textArea1, TextArea textArea2, LineChart chart) {
        this.weight = weight;
        this.value = value;
        this.capacity = capacity;
        this.chromoLen = chromoLen;
        this.scale = scale;
        this.maxGen = maxGen;
        this.pc = pc;
        this.pm = pm;
        this.textArea1=textArea1;
        this.textArea2=textArea2;
        this.chart=chart;
    }

    //初始化参数
    public void init(){
        //染色体的长度就是货物数量
        chromoLen=weight.length;
        bestGen=0;
        maxValue=0;
        bestIndividual=new int[chromoLen];
        curGen=0;
        initPopulation=new int[scale][chromoLen];
        newPopulation=new int[scale][chromoLen];
        fitness=new double[scale];
        pi=new double[scale];
        //初始化随机数产生器，以当前时间的毫秒值作为种子
        random=new Random(System.currentTimeMillis());

        vLine = new XYChart.Series<Number, Number>();
    }

    //初始化种群

    /**'
     * 初始化种群，用二维数组来存储种群
     */
    public void initPopulation(){
        for(int i=0;i<scale;i++){
            for(int j=0;j<chromoLen;j++){
                //给初始种群的个体赋值0/1，0表示当前货物不要，1表示当前货物要
                initPopulation[i][j]=random.nextInt(66536)%2;
            }
        }
    }

    /**
     * 计算给定个体的适应度
     * @param individual 给定个体的染色体编码
     * @return 该个体的适应度
     */
    public double fitnessForOne(int[] individual){
        double valueSum=0;
        double weightSum=0;
        for(int i=0;i<individual.length;i++){
            if(individual[i]==1){
                weightSum+=weight[i];
                valueSum+=value[i];
            }
        }
        // 如果重量超过背包的容量，返回0.否则返回当前价值和
        if(weightSum>capacity)  return 0;
        return valueSum;
    }

    // 计算种群所有个体的适应度,把结果放进fitness种群适应度数组
    public void fitnessForPopulation(){
        for(int i=0;i<scale;i++){
            fitness[i]=fitnessForOne(initPopulation[i]);
        }
    }


    // 计算种群中每个个体的累积概率pi，用于轮盘赌选择
    public void countRate(){
        int fitnessSum=0;//适应度总和
        for(int i=0;i<scale;i++){
            fitnessSum+=fitness[i];
        }

        pi[0]=(double)(fitness[0]/fitnessSum);
        for(int i=1;i<scale;i++){
            pi[i]=(double)((fitness[i]/fitnessSum)+pi[i-1]);
        }
//        for(double i:pi){
//            System.out.println(i);
//        }
    }


    /**
     * 选择当代种群中适应度最高的个体并把它加入新种群,并且把当前代数、最优个体的适应度及编码输出到文本域
     * @param gen
     */
    public void selectBestIndividual(int gen){
        int maxId=0;
        double max=fitness[0];
        for(int i=1;i<scale;i++){
            if(fitness[i]>max){
                max=fitness[i];
                maxId=i;
            }
        }
        System.out.println(max);
        textArea1.appendText("第"+gen+"次迭代的最优个体的适应度为："+max+"\n");

        //把坐标添加到折线图
        vLineData1 = new XYChart.Data<Number, Number>(gen,max);
        vLine.getData().add(vLineData1);

        StringBuilder sb=new StringBuilder();
        for(int i :initPopulation[maxId] ){
            sb.append(i);
        }
        sb.append("\n");
        textArea1.appendText("它的编码为"+sb.toString());


        //记录最优个体
        System.out.println(maxValue+"!!!");
        if(max>maxValue){
            maxValue=max;
            bestGen=gen;
            for(int j=0;j<chromoLen;j++){
                bestIndividual[j]=initPopulation[maxId][j];
            }
        }
        System.out.println(bestGen+"@@@");

        //最优个体添加到新种群
        addToNew(0,maxId);
    }

    public void addToNew(int now,int old){
        for(int i=0;i<chromoLen;i++){
            newPopulation[now][i]=initPopulation[old][i];
        }
    }


    /**
     * 轮盘赌选择剩下scale-1个个体
     */
    public void rouletteSelect(){
        double tempRan=0;//随机概率
        for(int i=1;i<scale;i++){
            tempRan=(double)(random.nextInt(65536)%100/100.0);
            System.out.println(tempRan+"?????");
//            for (double a:pi){
//                System.out.print(a+"/////");
//            }
            for(int j=0; j<scale;j++){
                if(tempRan<=pi[j]){//如果当前个体被选中了，添加到新种群
                    addToNew(i,j);
                    break;//一次只选一个，所以要break
                }
            }
        }
    }

    /**
     * 交叉方法,采用两点中间交叉的方式
     */
    public void intersect(){
        int begin=random.nextInt(65535)%chromoLen;//基因开始
        int end=random.nextInt(65535)%chromoLen;//结束
        while (begin==end){
            end=random.nextInt(65535)%chromoLen;
        }
        if(begin>end){
            int temp=begin;
            begin=end;
            end=temp;
        }
        for(int i=0;i<scale-1;i=i+2){
            for(int j=begin;j<=end;j++){
                if(pc>=Math.random()){//如果小于交叉概率pc，则进行交叉
                    int temp=newPopulation[i][j];
                    // 和下一个个体的当前基因交换
                    newPopulation[i][j]=newPopulation[i+1][j];
                    newPopulation[i+1][j]=temp;
                }
            }
        }
    }

    /**
     * 变异方法
     * 两次判断：染色体变异概率判断、当前基因变异概率判断
     */
    public void aberrance(){
        for(int i=0;i<scale;i++){
            //染色体变异概率
            if(pm>=Math.random()){
                for(int j=0;j<chromoLen;j++){
                    //每个基因变异概率
                    if(Math.random()<0.05){
                        newPopulation[i][j]=(newPopulation[i][j]==0)?1:0;
                    }
                }
            }
        }
    }

    public void copy(){
        initPopulation=newPopulation;
    }



    /**
     *  入口函数，调用其他方法实现种群的选择、交叉、变异、迭代过程
     */
    public void solution(){
        //初始化参数
        init();
        //初始化种群
        initPopulation();
        //迭代
        for(int i=0;i<maxGen;i++){
            //计算初始种群的适应度值
            fitnessForPopulation();
            //计算累计概率pi，用于轮盘赌
            countRate();
            //选择最优个体
            selectBestIndividual(i);
            //使用轮盘赌进行选择
            rouletteSelect();
            //交叉
            intersect();
            //变异
            aberrance();
            //把新种群赋值给init种群
            copy();
        }

//        System.out.println(maxValue);
//        for(int i:bestIndividual){
//            System.out.print(i+"----");
//        }
//        System.out.println(bestGen);
//        System.out.println(maxValue);
        textArea1.appendText("\n=============================================================================\n\n");
        textArea2.appendText("最优解出现在第"+bestGen+"代,该输出下背包能装的最大价值为"+maxValue+"\n");
        StringBuilder sb=new StringBuilder();
        for(int i :bestIndividual ){
            sb.append(i);
        }
        sb.append("\n");
        textArea2.appendText("它的解编码为"+sb.toString()+"====================================="+"\n");

        //折线图
//        XYChart.Data<Number, Number> vLineData1 = new XYChart.Data<Number, Number>(1,2);
//        XYChart.Data<Number, Number> vLineData2 = new XYChart.Data<Number, Number>(2,3);
//        vLine.getData().addAll(vLineData1,vLineData2);
        System.out.println(vLine.toString());
        chart.getData().add(vLine);
    }

//    测试代码
//    public static void main(String[] args) {
//        double[] w=new double[]{80,82,85,70,72,70,66,50,55,25,
//                50,55,40,48,50,32,22,60,30,32,
//                40,38,35,32,25,28,30,22,50,30,
//                45,30,60,50,20,65,20,25,30,10,
//                20,25,15,10,10,10,4,4,2,1};
//        double[] v=new double[]{220,208,198,192,180,180,165,162,160,158,
//                155,130,125,122,120,118,115,110,105,101,
//                100,100,98,96,95,90,88,82,80,77,
//                75,73,72,70,69,66,65,63,60,58,
//                56,50,30,20,15,10,8,5,3,1};
//
//        GA_knapsack ga_knapsack=new GA_knapsack(w,v,1000,50,1000,0.7,0.05,);
//        ga_knapsack.solution();
//    }












}
