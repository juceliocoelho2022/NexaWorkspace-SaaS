export type Auth = { token:string; tenantId:string; userId:string; name:string; email:string; role:string; plan:string }
export type Project = { id:string; name:string; description?:string; status:'PLANNING'|'ACTIVE'|'ON_HOLD'|'COMPLETED'; createdAt:string; updatedAt:string }
export type Dashboard = { projects:number; activeProjects:number; members:number; plan:string; role:string; userName:string }
