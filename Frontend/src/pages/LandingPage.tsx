import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import "../styles/landing.css";

const fadeUp = {
    hidden:  { opacity: 0, y: 40 },
    visible: { opacity: 1, y: 0 }
};

const stagger = {
    hidden:  {},
    visible: { transition: { staggerChildren: 0.15 } }
};

const scaleIn = {
    hidden:  { opacity: 0, scale: 0.85 },
    visible: { opacity: 1, scale: 1 }
};

export default function LandingPage() {
    const navigate = useNavigate();

    return (
        <div className="landing-page">

            <motion.nav
                className="landing-nav"
                initial={{ y: -80, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ duration: 0.6, ease: "easeOut" }}
            >
                <div className="landing-nav-brand">
                    <motion.div
                        className="landing-nav-logo"
                        initial={{ rotate: -180, scale: 0 }}
                        animate={{ rotate: 0, scale: 1 }}
                        transition={{ duration: 0.7, delay: 0.3, type: "spring", stiffness: 200 }}
                    >
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <path d="M9 3H5a2 2 0 00-2 2v4m6-6h10a2 2 0 012 2v4M9 3v18m0 0h10a2 2 0 002-2V9M9 21H5a2 2 0 01-2-2V9m0 0h18" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <circle cx="15" cy="15" r="3" stroke="white" strokeWidth="2"/>
                            <path d="M17.5 17.5L20 20" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                        </svg>
                    </motion.div>
                    <div>
                        <span className="landing-nav-name">SolveMate</span>
                        <span className="landing-nav-tagline">Lab Intelligence System</span>
                    </div>
                </div>
                <motion.div
                    className="landing-nav-actions"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: 0.5, duration: 0.4 }}
                >
                    <button className="landing-nav-login" onClick={() => navigate("/login")}>
                        Login
                    </button>
                    <button className="landing-nav-register" onClick={() => navigate("/register")}>
                        Get Started
                    </button>
                </motion.div>
            </motion.nav>

            <section className="landing-hero">
                <div className="blob blob-1" />
                <div className="blob blob-2" />
                <div className="blob blob-3" />

                <div className="landing-hero-content">
                    <motion.div
                        variants={stagger}
                        initial="hidden"
                        animate="visible"
                        transition={{ delayChildren: 0.4 }}
                    >
                        <motion.div
                            className="landing-hero-badge"
                            variants={scaleIn}
                            transition={{ duration: 0.5, ease: "easeOut" }}
                        >
                            <span className="badge-dot" />
                            ML-Powered Polymer-Solvent Compatibility
                        </motion.div>

                        <motion.h1
                            className="landing-hero-title"
                            variants={fadeUp}
                            transition={{ duration: 0.6, ease: "easeOut" }}
                        >
                            Find the Right Solvent<br />
                            <span className="landing-hero-highlight">Instantly.</span>
                        </motion.h1>

                        <motion.p
                            className="landing-hero-subtitle"
                            variants={fadeUp}
                            transition={{ duration: 0.6, ease: "easeOut" }}
                        >
                            SolveMate uses a Random Forest ML model trained on 800 real
                            polymer-solvent pairs to predict compatibility and recommend
                            the Top 5 solvents — in seconds, not days.
                        </motion.p>

                        <motion.div
                            className="landing-hero-actions"
                            variants={fadeUp}
                            transition={{ duration: 0.6, ease: "easeOut" }}
                        >
                            <motion.button
                                className="landing-btn-primary"
                                onClick={() => navigate("/register")}
                                whileHover={{ scale: 1.04, y: -2 }}
                                whileTap={{ scale: 0.97 }}
                            >
                                Get Started
                                <span className="btn-arrow">→</span>
                            </motion.button>
                            <motion.button
                                className="landing-btn-secondary"
                                onClick={() => navigate("/login")}
                                whileHover={{ scale: 1.03 }}
                                whileTap={{ scale: 0.97 }}
                            >
                                Login to Dashboard
                            </motion.button>
                        </motion.div>
                    </motion.div>
                </div>
            </section>

            <motion.footer
                className="landing-footer"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 1.0, duration: 0.6 }}
            >
                <div className="landing-footer-inner">
                    <div className="landing-footer-brand">
                        <div className="landing-footer-logo">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                                <path d="M9 3H5a2 2 0 00-2 2v4m6-6h10a2 2 0 012 2v4M9 3v18m0 0h10a2 2 0 002-2V9M9 21H5a2 2 0 01-2-2V9m0 0h18" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                <circle cx="15" cy="15" r="3" stroke="white" strokeWidth="2"/>
                                <path d="M17.5 17.5L20 20" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                            </svg>
                        </div>
                        <div>
                            <p className="landing-footer-name">SolveMate</p>
                            <p className="landing-footer-sub">Solvent Filter & Catalog Browser</p>
                        </div>
                    </div>
                    <div className="landing-footer-info">
                        <p>Group 03 · Department of IT, FMSC, USJ</p>
                        <p>OOP Project — 2026</p>
                    </div>
                </div>
            </motion.footer>

        </div>
    );
}
