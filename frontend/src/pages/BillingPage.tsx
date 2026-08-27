import { useState } from 'react'
import { Check, Crown, CreditCard, ExternalLink, ShieldCheck } from 'lucide-react'
import { api, type CheckoutProvider } from '../services/api'

export function BillingPage({current,mode,reload}:{current:string;mode:'SANDBOX'|'LIVE';reload:()=>Promise<void>}){
  const [provider,setProvider]=useState<CheckoutProvider>('STRIPE')
  const [busy,setBusy]=useState<string|null>(null)
  const [message,setMessage]=useState('')
  const [error,setError]=useState('')
  const plans=[
    {name:'FREE',price:'R$ 0',items:['3 projetos','1 membro','Dashboard básico']},
    {name:'PRO',price:'R$ 49,90/mês',items:['Projetos ilimitados','10 membros','Auditoria']},
    {name:'BUSINESS',price:'R$ 149,90/mês',items:['Membros ilimitados','SLA e suporte','Integrações enterprise']}
  ]

  async function choose(plan:string){
    setBusy(plan);setError('');setMessage('')
    try{
      if(mode==='SANDBOX'){
        await api.changePlan(plan)
        await reload()
        setMessage(`Plano ${plan} aplicado em sandbox. Nenhuma cobrança foi realizada.`)
        return
      }
      if(plan==='FREE'){
        setError('No modo LIVE, downgrade para FREE deve ser confirmado pelo fluxo de cancelamento e webhook do provedor.')
        return
      }
      const checkout=await api.checkout(plan,provider)
      if(checkout.sandbox){setMessage('Checkout sandbox criado com sucesso.')}else{window.location.href=checkout.checkoutUrl}
    }catch(e){setError(e instanceof Error?e.message:'Falha no billing')}finally{setBusy(null)}
  }

  return <>
    <div className="pageHead">
      <div><span className="eyebrow">SUBSCRIPTION</span><h1>Planos</h1><p>Billing {mode==='SANDBOX'?'sandbox seguro':'LIVE'} com Stripe e Mercado Pago.</p></div>
      <span className="planPill"><ShieldCheck/> {mode}</span>
    </div>

    <div className="card projectForm">
      <span className="eyebrow">PAYMENT PROVIDER</span>
      <h3>Gateway de pagamento</h3>
      <p className="muted">Escolha o provedor usado para novos checkouts pagos.</p>
      <div className="actions">
        <button className={provider==='STRIPE'?'primary':'secondary'} onClick={()=>setProvider('STRIPE')}><CreditCard/> Stripe</button>
        <button className={provider==='MERCADO_PAGO'?'primary':'secondary'} onClick={()=>setProvider('MERCADO_PAGO')}><CreditCard/> Mercado Pago</button>
      </div>
    </div>

    {message&&<div className="card projectForm"><strong>{message}</strong></div>}
    {error&&<div className="error">{error}</div>}

    <div className="plans">{plans.map(p=><div className={`card plan ${current===p.name?'selected':''}`} key={p.name}>
      {current===p.name&&<span className="current"><Crown/>Atual</span>}
      <h3>{p.name}</h3><strong>{p.price}</strong>
      <ul>{p.items.map(x=><li key={x}><Check/>{x}</li>)}</ul>
      <button className={current===p.name?'secondary':'primary'} disabled={current===p.name||busy!==null} onClick={()=>choose(p.name)}>
        {current===p.name?'Plano atual':busy===p.name?'Processando...':mode==='LIVE'&&p.name!=='FREE'?<>Abrir checkout <ExternalLink/></>:'Selecionar'}
      </button>
    </div>)}</div>
  </>
}
