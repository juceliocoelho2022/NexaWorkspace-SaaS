import type { Auth, Dashboard, Project } from '../types'
const KEY='nexa_auth'
export const authStore={get:()=>{try{return JSON.parse(localStorage.getItem(KEY)||'null') as Auth|null}catch{return null}},set:(a:Auth)=>localStorage.setItem(KEY,JSON.stringify(a)),clear:()=>localStorage.removeItem(KEY)}
async function request<T>(url:string,options:RequestInit={}):Promise<T>{const a=authStore.get();const headers=new Headers(options.headers);headers.set('Content-Type','application/json');if(a?.token)headers.set('Authorization',`Bearer ${a.token}`);const res=await fetch(url,{...options,headers});if(res.status===401){authStore.clear();location.reload()}if(!res.ok){const e=await res.json().catch(()=>({message:'Erro inesperado'}));throw new Error(e.message||'Erro na requisição')}if(res.status===204)return undefined as T;return res.json()}
export const api={
 register:(body:{companyName:string;name:string;email:string;password:string})=>request<Auth>('/api/auth/register',{method:'POST',body:JSON.stringify(body)}),
 login:(body:{email:string;password:string})=>request<Auth>('/api/auth/login',{method:'POST',body:JSON.stringify(body)}),
 dashboard:()=>request<Dashboard>('/api/dashboard'),
 projects:()=>request<Project[]>('/api/projects'),
 createProject:(body:Partial<Project>)=>request<Project>('/api/projects',{method:'POST',body:JSON.stringify(body)}),
 deleteProject:(id:string)=>request<void>(`/api/projects/${id}`,{method:'DELETE'}),
 billing:()=>request<{plan:string;status:string}>('/api/billing'),
 changePlan:(plan:string)=>request<{plan:string;status:string}>('/api/billing/plan',{method:'PATCH',body:JSON.stringify({plan})})
}
