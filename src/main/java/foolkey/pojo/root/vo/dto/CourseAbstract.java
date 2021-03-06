package foolkey.pojo.root.vo.dto;

import foolkey.pojo.root.vo.AbstractDTO;
import foolkey.pojo.root.vo.assistObject.CourseTimeDayEnum;
import foolkey.pojo.root.vo.assistObject.TeachMethodEnum;
import foolkey.pojo.root.vo.assistObject.TechnicTagEnum;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by admin on 2017/4/24.
 */
@Component
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CourseAbstract  extends AbstractDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //创建者
    @Column(name = "creator_id")
    private Long creatorId;

    //技术标签
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "technic_tag")
    private TechnicTagEnum technicTagEnum;

    //名字
    @Column(name = "topic")
    private String topic;

    //描述
    @Column(name = "description")
    private String description;

    //定价，虚拟币
    @Column(name = "price")
    private Double price;

    //授课方式
    @Column(name = "teach_method")
    @Enumerated(EnumType.ORDINAL)
    private TeachMethodEnum teachMethodEnum;

    //授课时间
    @Column(name ="class_time")
    @Enumerated(EnumType.ORDINAL)
    private CourseTimeDayEnum courseTimeDayEnum;

    //创建时间
    @Column(name = "create_time")
    private Date createTime;


    @Override
    public String toString() {
        return "CourseAbstract{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", technicTagEnum=" + technicTagEnum +
                ", topic='" + topic + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", teachMethodEnum=" + teachMethodEnum +
                ", courseTimeDayEnum=" + courseTimeDayEnum +
                ", createTime=" + createTime +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public TechnicTagEnum getTechnicTagEnum() {
        return technicTagEnum;
    }

    public void setTechnicTagEnum(TechnicTagEnum technicTagEnum) {
        this.technicTagEnum = technicTagEnum;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public TeachMethodEnum getTeachMethodEnum() {
        return teachMethodEnum;
    }

    public void setTeachMethodEnum(TeachMethodEnum teachMethodEnum) {
        this.teachMethodEnum = teachMethodEnum;
    }

    public CourseTimeDayEnum getCourseTimeDayEnum() {
        return courseTimeDayEnum;
    }

    public void setCourseTimeDayEnum(CourseTimeDayEnum courseTimeDayEnum) {
        this.courseTimeDayEnum = courseTimeDayEnum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
