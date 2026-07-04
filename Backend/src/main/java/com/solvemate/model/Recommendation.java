package com.solvemate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polymer_id", nullable = false)
    private Polymer polymer;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "recomm_id")
    private List<CompatibilityResult> rankedSolvents = new ArrayList<>();

    private String rankCriteria;

    private LocalDateTime generatedAt;



    public Recommendation() {
        this.generatedAt  = LocalDateTime.now();
        this.rankCriteria = "RED < 1.2, ranked by ML probability";
    }

    public Recommendation(Polymer polymer, List<CompatibilityResult> top5) {
        this();
        this.polymer        = polymer;
        this.rankedSolvents = top5;
    }




    public List<CompatibilityResult> generateTop5() {
        return rankedSolvents.stream()
                .filter(r -> r.getRankPosition() <= 5)
                .toList();
    }


    public void rankSolvents() {
        for (int i = 0; i < rankedSolvents.size(); i++) {
            rankedSolvents.get(i).setRankPosition(i + 1);
        }
    }



    public Long getRecommId()                             { return recommId; }
    public Polymer getPolymer()                           { return polymer; }
    public List<CompatibilityResult> getRankedSolvents()  { return rankedSolvents; }
    public String getRankCriteria()                       { return rankCriteria; }
    public LocalDateTime getGeneratedAt()                 { return generatedAt; }

    public void setPolymer(Polymer polymer)                               { this.polymer = polymer; }
    public void setRankedSolvents(List<CompatibilityResult> rankedSolvents){ this.rankedSolvents = rankedSolvents; }
    public void setRankCriteria(String rankCriteria)                       { this.rankCriteria = rankCriteria; }
}
