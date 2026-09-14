import DashboardPage from './pages/DashboardPage'

/** 단일 대시보드 작업영역을 제공하는 애플리케이션 셸이다. */
export default function App() {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark" aria-hidden="true">A</span>
          <span>Lab Aster</span>
        </div>

        <nav aria-label="주요 메뉴">
          <button className="nav-item active" type="button" aria-current="page">
            <span className="nav-icon" aria-hidden="true">⌂</span>
            대시보드
          </button>
        </nav>

        <div className="sidebar-footer">
          <span className="status-dot" aria-hidden="true" />
          Local
        </div>
      </aside>

      <main className="workspace">
        <header className="topbar">
          <div>
            <p className="breadcrumb">Workspace / Dashboard</p>
            <h1>대시보드</h1>
          </div>
          <span className="environment">Local</span>
        </header>
        <DashboardPage />
      </main>
    </div>
  )
}
