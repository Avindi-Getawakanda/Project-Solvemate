
const BASE         = "http://localhost:8080/api";
const USERS_URL    = `${BASE}/users`;
const POLYMERS_URL = `${BASE}/polymers`;
const SOLVENTS_URL = `${BASE}/solvents`;
const COMPAT_URL   = `${BASE}/compatibility`;
const AI_URL       = `${BASE}/ai`;
const DASHBOARD_URL = `${BASE}/dashboard`;

async function apiFetch<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, { headers: { "Content-Type": "application/json" }, ...options });
  const result = await response.json();
  if (!response.ok) throw new Error(result.message || "Request failed");
  return result as T;
}

export interface RegisterData { fullName: string; email: string; password: string; role: string; }
export interface LoginData { email: string; password: string; }
export interface UserResponse { userId: number; fullName: string; email: string; role: string; status: string; }
export interface PolymerData { polymerName: string; polymerCategory: string; deltaD: number; deltaP: number; deltaH: number; r0: number; }
export interface PolymerResponse { polymerId: number; polymerName: string; polymerCategory: string; deltaD: number; deltaP: number; deltaH: number; r0: number; deltaT: number; }
export interface SolventData { name: string; chemicalFormula?: string; deltaD: number; deltaP: number; deltaH: number; molarVolume?: number; costPerLiter?: number; envImpactScore: string; toxicityLevel?: number; euBanStatus?: boolean; }
export interface SolventResponse { solventId: number; name: string; chemicalFormula: string; deltaD: number; deltaP: number; deltaH: number; molarVolume: number; deltaT: number; costPerLiter: number; envImpactScore: string; toxicityLevel: number; euBanStatus: boolean; }
export interface ShapExplanation { feature: string; label: string; shapValue: number; contribution: number; plainEnglish?: string; }
export interface RecommendationBriefing {
  compatibilityAssessment: string;
  healthSafetyAssessment: string;
  environmentalImpactAssessment: string;
  regulatoryComplianceAssessment: string;
  costPracticalityAssessment: string;
  overallRecommendation: string;
  saferAlternative?: string | null;
}
export interface CompatibilityResult { resultId?: number; polymerId: number; polymerName: string; solventId: number; solventName: string; deltaDDifference?: number; deltaPDifference?: number; deltaHDifference?: number; raValue?: number; redValue?: number; mlProbability: number; compatibilityScore: number; rankPosition: number; result: string; recommendationType?: string; solventsAnalysed?: number; explanation?: ShapExplanation[]; greenScore?: number; envImpactScore?: string; euBanStatus?: boolean; greenInsight?: string; briefing?: RecommendationBriefing; }

export interface AnalysisSummary {
  solventsAnalysed: number;
  highConfidenceCount: number;
  moderateCount: number;
  lowCount: number;
  redCompatibleCount: number;
  topProbability: number;
  medianProbability: number;
  greenModeActive: boolean;
  averageGreenScore: number;
}

export interface CompatibilityAnalysis {
  recommended: CompatibilityResult[];
  notRecommended: CompatibilityResult[];
  summary: AnalysisSummary;
}

export interface DashboardStats {
  polymerCount: number;
  solventCount: number;
  trialCount: number;
  reportCount: number;
  userCount: number;
  recentTrials: { trialId: number; polymerName: string; solventName: string; trialResult: string; trialDate: string; }[];
  topSolvents: { solventName: string; mlProbability: number; }[];
}

export const getDashboardStats = (): Promise<DashboardStats> => apiFetch(`${DASHBOARD_URL}/stats`);

export const registerUser = (data: RegisterData) => apiFetch(`${USERS_URL}/register`, { method: "POST", body: JSON.stringify(data) });
export const loginUser = (data: LoginData) => apiFetch(`${USERS_URL}/login`, { method: "POST", body: JSON.stringify(data) });
export const logoutUser = () => apiFetch(`${USERS_URL}/logout`, { method: "POST" });
export const getAllUsers = (): Promise<UserResponse[]> => apiFetch(`${USERS_URL}/all`);

export const getAllPolymers = (): Promise<PolymerResponse[]> => apiFetch(POLYMERS_URL);
export const getPolymerById = (id: number): Promise<PolymerResponse> => apiFetch(`${POLYMERS_URL}/${id}`);
export const addPolymer = (data: PolymerData): Promise<PolymerResponse> => apiFetch(POLYMERS_URL, { method: "POST", body: JSON.stringify(data) });
export const updatePolymer = (id: number, data: PolymerData): Promise<PolymerResponse> => apiFetch(`${POLYMERS_URL}/${id}`, { method: "PUT", body: JSON.stringify(data) });
export const deletePolymer = (id: number) => apiFetch(`${POLYMERS_URL}/${id}`, { method: "DELETE" });

export const getAllSolvents = (): Promise<SolventResponse[]> => apiFetch(SOLVENTS_URL);
export const getSolventById = (id: number): Promise<SolventResponse> => apiFetch(`${SOLVENTS_URL}/${id}`);
export const addSolvent = (data: SolventData): Promise<SolventResponse> => apiFetch(SOLVENTS_URL, { method: "POST", body: JSON.stringify(data) });
export const updateSolvent = (id: number, data: SolventData): Promise<SolventResponse> => apiFetch(`${SOLVENTS_URL}/${id}`, { method: "PUT", body: JSON.stringify(data) });
export const deleteSolvent = (id: number) => apiFetch(`${SOLVENTS_URL}/${id}`, { method: "DELETE" });

export const runCompatibilityAnalysis = (polymerId: number, greenMode = false): Promise<CompatibilityAnalysis> =>
    apiFetch(`${COMPAT_URL}/recommend/${polymerId}?green=${greenMode}`, { method: "POST" });

/** @deprecated use runCompatibilityAnalysis */
export const recommendTop5 = runCompatibilityAnalysis;
export const getCompatibilityResults = (polymerId: number): Promise<CompatibilityResult[]> => apiFetch(`${COMPAT_URL}/results/${polymerId}`);



export interface TrialData {
  polymerName: string;
  solventName: string;
  trialResult: string;
  outcomeObservation?: string;
  temperature?: number;
  concentration?: number;
  performedBy?: string;
}

export interface TrialResponse {
  trialId: number;
  polymerName: string;
  solventName: string;
  trialResult: string;
  outcomeObservation: string;
  temperature: number;
  concentration: number;
  performedBy: string;
  trialDate: string;
}

export interface ReportResponse {
  id: number;
  polymerName: string;
  solventName: string;
  compatibilityScore: number;
  trialResult: string;
  outcomeObservation: string;
  costAnalysis: number;
  envImpactSummary: string;
  euComplianceStatus: string;
  finalDecision: string;
}

export interface AiChatResponse {
  answer: string;
}

export const askAiAssistant = (polymerId: number, question: string): Promise<AiChatResponse> =>
    apiFetch(`${AI_URL}/ask`, { method: "POST", body: JSON.stringify({ polymerId, question }) });


export const getAllTrials = (): Promise<TrialResponse[]> =>
    apiFetch(`${BASE}/trials`);

export const recordTrial = (data: TrialData): Promise<TrialResponse> =>
    apiFetch(`${BASE}/trials`, { method: "POST", body: JSON.stringify(data) });

export const getAllReports = (): Promise<ReportResponse[]> =>
    apiFetch(`${BASE}/reports`);