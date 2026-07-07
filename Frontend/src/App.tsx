import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import AdminDashboard from "./pages/AdminDashboard";
import UserDashboard from "./pages/UserDashboard";

function RootRedirect() {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    if (user.role === "ADMIN") return <Navigate to="/admin" replace />;
    if (user.role === "LAB_USER") return <Navigate to="/user" replace />;
    return <Navigate to="/login" replace />;
}

function ProtectedAdmin({ children }: { children: React.ReactNode }) {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    if (!user.role) return <Navigate to="/login" replace />;
    if (user.role !== "ADMIN") return <Navigate to="/user" replace />;
    return <>{children}</>;
}

function ProtectedUser({ children }: { children: React.ReactNode }) {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    if (!user.role) return <Navigate to="/login" replace />;
    if (user.role !== "LAB_USER") return <Navigate to="/admin" replace />;
    return <>{children}</>;
}

export default function App() {
    return (
         <BrowserRouter>
                    <Routes>
                        <Route path="/"         element={<RootRedirect />} />
                        <Route path="/login"    element={<Login />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/admin"    element={<ProtectedAdmin><AdminDashboard /></ProtectedAdmin>} />
                        <Route path="/user"     element={<ProtectedUser><UserDashboard /></ProtectedUser>} />
                        <Route path="*"         element={<Navigate to="/" replace />} />
                    </Routes>
                </BrowserRouter>
    );
}
