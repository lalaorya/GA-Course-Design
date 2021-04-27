package sample;

public class GoodsBean {
    // 物品的抽象类
    private String id;
    private String weight;
    private String value;

    public GoodsBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public GoodsBean(String id, String weight, String value) {
        this.id = id;
        this.weight = weight;
        this.value = value;
    }

    @Override
    public String toString() {
        return "GoodsBean{" +
                "id='" + id + '\'' +
                ", weight='" + weight + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
