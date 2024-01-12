package com.example.project;

public class item {
    private String criterea, treatment, diagnosis;


    public item(String critirea, String treatment, String diagnosis) {
        this.criterea = critirea;
        this.treatment = treatment;
        this.diagnosis = diagnosis;
    }

    public void setCritirea(String critirea) {
        this.criterea = critirea;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getCriterea() {
        return criterea;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getDiagnosis() {
        return diagnosis;
    }
}