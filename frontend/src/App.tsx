import { useEffect, useState } from 'react'
import { AuthPage } from './pages/AuthPage'
import { DashboardPage } from './pages/DashboardPage'
import { ProjectsPage } from './pages/ProjectsPage'
import { BillingPage } from './pages/BillingPage'
import { Sidebar, type Page } from './components/Sidebar'
import { api, authStore } from './services/api'
import type { Dashboard, Project } from './types'
export default function App(){const [auth,setAuth]=useState(authStore.get());const [page,setPage]=useState<Page>('dashboard');const [dashboard,setDashboard]=useState<Dashboard|null>(null);const [projects,setProjects]=useState<Project[]>([]);const [plan,setPlan]=useState(auth?.plan||'FREE');async function load(){if(!authStore.get())return;const [d,p,b]=await Promise.all([api.dashboard(),api.projects(),api.billing()]);setDashboard(d);setProjects(p);setPlan(b.plan)}useEffect(()=>{load().catch(console.error)},[auth]);if(!auth)return <AuthPage onDone={()=>setAuth(authStore.get())}/>;return <div className="appShell"><Sidebar page={page} setPage={setPage} onLogout={()=>{authStore.clear();setAuth(null)}}/><main className="content">{page==='dashboard'&&<DashboardPage data={dashboard}/>} {page==='projects'&&<ProjectsPage projects={projects} reload={async()=>setProjects(await api.projects())}/>} {page==='billing'&&<BillingPage current={plan} reload={async()=>setPlan((await api.billing()).plan)}/>}</main></div>}
