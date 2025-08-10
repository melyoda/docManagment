package com.mawkszuxz.docmanagment.util;

import java.util.List;
import java.util.Map;

public class ClassificationTree {
    public static CategoryNode buildTree() {
        return new CategoryNode("Root", Map.of(), List.of(
                // --- SCIENCE ---
                new CategoryNode("Science",
                        Map.of("science", 1.0, "research", 1.5, "discovery", 1.5), List.of(
                        new CategoryNode("Physics",
                                Map.of("physics", 2.0, "mechanics", 3.0, "relativity", 4.0, "quantum", 5.0, "thermodynamics", 5.0, "einstein", 4.5), List.of()),
                        new CategoryNode("Chemistry",
                                Map.of("chemistry", 2.0, "molecule", 3.0, "reaction", 3.5, "element", 4.0, "compound", 4.0, "periodic table", 5.0), List.of()),
                        new CategoryNode("Biology",
                                Map.of("biology", 2.0, "organism", 2.5, "cell", 3.0, "evolution", 4.0, "genetics", 5.0, "dna", 5.0), List.of()),
                        new CategoryNode("Astronomy",
                                Map.of("astronomy", 2.0, "star", 2.5, "planet", 3.0, "galaxy", 4.0, "cosmos", 4.5, "black hole", 5.0), List.of())
                )),

                // --- TECHNOLOGY ---
                new CategoryNode("Technology",
                        Map.of("technology", 1.0, "tech", 1.0, "innovation", 1.5, "engineering", 2.0), List.of(
                        new CategoryNode("Programming",
                                Map.of("code", 1.0, "software", 1.5, "development", 1.5, "algorithm", 3.0), List.of(
                                new CategoryNode("Web Development",
                                        Map.of("html", 2.5, "css", 2.5, "javascript", 3.5, "react", 4.5, "angular", 4.5, "node.js", 5.0), List.of()),
                                new CategoryNode("Mobile Development",
                                        Map.of("android", 3.0, "ios", 3.0, "swift", 4.5, "kotlin", 4.5, "flutter", 5.0), List.of()),
                                new CategoryNode("Backend",
                                        Map.of("java", 2.5, "python", 2.5, "api", 3.5, "database", 3.5, "sql", 4.0, "spring boot", 5.0, "django", 5.0), List.of())
                        )),
                        new CategoryNode("Artificial Intelligence",
                                Map.of("ai", 2.0, "artificial intelligence", 3.0, "machine learning", 4.0, "ml", 4.0, "deep learning", 5.0, "neural network", 5.0), List.of()),
                        new CategoryNode("Networking",
                                Map.of("network", 2.0, "router", 3.0, "firewall", 4.0, "dns", 4.5, "tcp", 5.0, "ip address", 5.0), List.of()),
                        new CategoryNode("Cybersecurity",
                                Map.of("cybersecurity", 2.5, "infosec", 3.0, "malware", 4.0, "encryption", 4.5, "phishing", 5.0, "vulnerability", 5.0), List.of())
                )),

                // --- BUSINESS & FINANCE ---
                new CategoryNode("Business & Finance",
                        Map.of("business", 1.0, "finance", 1.5, "corporate", 2.0, "company", 1.5), List.of(
                        new CategoryNode("Economics",
                                Map.of("economics", 2.0, "gdp", 3.5, "inflation", 4.0, "recession", 4.5, "supply chain", 3.5, "market", 3.0), List.of()),
                        new CategoryNode("Investing",
                                Map.of("investment", 2.0, "stocks", 3.0, "bonds", 3.0, "portfolio", 4.0, "wall street", 4.5, "securities", 5.0), List.of()),
                        new CategoryNode("Marketing",
                                Map.of("marketing", 2.0, "advertising", 2.5, "brand", 3.0, "campaign", 3.5, "seo", 5.0, "social media", 4.0), List.of()),
                        new CategoryNode("Accounting",
                                Map.of("accounting", 2.5, "audit", 4.0, "balance sheet", 4.5, "gaap", 5.0, "bookkeeping", 3.0), List.of())
                )),

                // --- ARTS & HUMANITIES ---
                new CategoryNode("Arts & Humanities",
                        Map.of("art", 1.0, "humanities", 1.5, "culture", 2.0), List.of(
                        new CategoryNode("Literature",
                                Map.of("literature", 2.0, "novel", 3.0, "poem", 3.5, "fiction", 2.5, "essay", 3.0, "shakespeare", 5.0), List.of()),
                        new CategoryNode("History",
                                Map.of("history", 2.0, "ancient", 3.0, "medieval", 3.5, "revolution", 4.0, "war", 4.5, "civilization", 3.5), List.of()),
                        new CategoryNode("Philosophy",
                                Map.of("philosophy", 2.0, "ethics", 3.5, "metaphysics", 4.0, "logic", 3.0, "socrates", 5.0, "plato", 5.0, "nietzsche", 5.0), List.of())
                )),

                // --- LAW & GOVERNMENT ---
                new CategoryNode("Law & Government",
                        Map.of("law", 1.5, "legal", 2.0, "government", 1.5, "policy", 2.5), List.of(
                        new CategoryNode("Legislation",
                                Map.of("legislation", 2.5, "bill", 3.5, "statute", 4.0, "regulation", 4.5, "act", 4.0), List.of()),
                        new CategoryNode("Litigation",
                                Map.of("litigation", 3.0, "lawsuit", 3.5, "court", 3.0, "plaintiff", 4.5, "defendant", 4.5, "verdict", 5.0), List.of())
                ))
        ));
    }
}