import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../services/api";
import SolveMateLogo from "../components/SolveMateLogo";
import "../styles/auth.css";

export default function Register() {
  const navigate = useNavigate();
  const [fullName, setFullName]             = useState("");
  const [email, setEmail]                   = useState("");
  const [role, setRole]                     = useState("LAB_USER");
  const [password, setPassword]             = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError]                   = useState("");
  const [success, setSuccess]               = useState("");
  const [loading, setLoading]               = useState(false);

  const handleRegister = async () => {
    setError(""); setSuccess("");
    if (!fullName || !email || !password || !confirmPassword || !role) { setError("Please fill all fields"); return; }
    if (password !== confirmPassword) { setError("Passwords do not match"); return; }
    try {
      setLoading(true);
      const result = await registerUser({ fullName, email, password, role }) as { message: string };
      setSuccess(result.message || "Registration successful");
      setTimeout(() => navigate("/"), 1000);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Registration failed");
    } finally { setLoading(false); }
  };

  return (
      <div className="auth-container">
        <div className="auth-card">
          <div className="auth-logo-mark">
            <SolveMateLogo size={26} />
          </div>
          <h1>Create Account</h1>
          <p>Register for SolveMate</p>
          {error && <p className="error-text">{error}</p>}
          {success && <p className="success-text">{success}</p>}
          <input type="text" placeholder="Full Name" value={fullName} onChange={e => setFullName(e.target.value)} />
          <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
          <select value={role} onChange={e => setRole(e.target.value)}>
            <option value="LAB_USER">LAB_USER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
          <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
          <input type="password" placeholder="Confirm Password" value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)} />
          <button onClick={handleRegister} disabled={loading}>{loading ? "Registering..." : "Register"}</button>
          <p>Already have an account? <Link to="/">Login</Link></p>
        </div>
      </div>
  );
}
