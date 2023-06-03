package dku.cloudcomputing.surveyserver.entity.survey;

import dku.cloudcomputing.surveyserver.entity.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Survey {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String name;
    private LocalDate startDate;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private boolean visibility;

    @OneToMany(mappedBy = "survey")
    private List<SurveyDetail> surveyDetails = new ArrayList<>();

    public Survey(Member member, String name, int duration, boolean visibility) {
        this.name = name;
        this.member = member;
        this.startDate = LocalDate.now();
        this.duration = duration;
        this.visibility = visibility;
    }
}
