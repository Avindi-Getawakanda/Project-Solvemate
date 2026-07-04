import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LandingPage from "./pages/LandingPage";
import Login from "./pages/Login";
import Register from "./pages/Register";
import AdminDashboard from "./pages/AdminDashboard";
import UserDashboard from "./pages/UserDashboard";

function ProtectedAdmin({ children }: { children: React.ReactNode }) {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    if (!user.role) return <Navigate to="/" replace />;
    if (user.role !== "ADMIN") return <Navigate to="/user" replace />;
    return <>{children}</>;
}

function ProtectedUser({ children }: { children: React.ReactNode }) {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    if (!user.role) return <Navigate to="/" replace />;
    if (user.role !== "LAB_USER") return <Navigate to="/admin" replace />;
    return <>{children}</>;
}

export default function App() {
    return (
         <BrowserRouter>
                    <Routes>
                        <Route path="/"         element={<LandingPage />} />
                        <Route path="/login"    element={<Login />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/admin"    element={<AdminDashboard />} />
                        <Route path="/user"     element={<UserDashboard />} />
                        <Route path="*"         element={<Navigate to="/" replace />} />
                    </Routes>
                </BrowserRouter>
    );
}
