package foolkey.pojo.root.vo.dto;

import foolkey.pojo.root.vo.assistObject.ApplicationStateEnum;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

/**
 * 申请
 * Created by admin on 2017/4/24.
 */
@Component
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ApplicationAbstract {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    //申请人
    @Column(name = "applicant_id")
    private Long applicantId;

    //messageId，暂时不做
    @Column(name = "message_id")
    private Long messageId;

    //申请的时间
    @Column(name = "apply_time")
    private Date applyTime;

    //状态 agree, refuse, expired, 取消课程, processing
    @Column(name = "state")
    @Enumerated(EnumType.ORDINAL)
    private ApplicationStateEnum state;

    @Override
    public String toString() {
        return "ApplicationAbstract{" +
                "id=" + id +
                ", applicantId=" + applicantId +
                ", messageId=" + messageId +
                ", applyTime=" + applyTime +
                ", state=" + state +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public ApplicationStateEnum getState() {
        return state;
    }

    public void setState(ApplicationStateEnum state) {
        this.state = state;
    }
}
