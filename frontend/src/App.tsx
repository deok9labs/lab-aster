import { useState } from 'react'

type MenuId = 'dashboard' | 'work' | 'work-list' | 'work-board' | 'schedule' | 'settings'
type NavigationIconName = 'dashboard' | 'work' | 'list' | 'board' | 'schedule' | 'settings'

const ICON_PATHS: Record<NavigationIconName, string[]> = {
  dashboard: ['M4 4h6v6H4z', 'M14 4h6v6h-6z', 'M4 14h6v6H4z', 'M14 14h6v6h-6z'],
  work: ['M5 4h14v16H5z', 'm8 9 2 2 4-4'],
  list: ['M9 6h10', 'M9 12h10', 'M9 18h10', 'M5 6h.01', 'M5 12h.01', 'M5 18h.01'],
  board: ['M4 5h6v14H4z', 'M14 5h6v8h-6z'],
  schedule: ['M5 4h14v16H5z', 'M8 2v4', 'M16 2v4', 'M5 9h14'],
  settings: ['M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8', 'M12 2v3', 'M12 19v3', 'M4.9 4.9 7 7', 'm17 17 2.1 2.1', 'M2 12h3', 'M19 12h3'],
}

function NavigationIcon({ name }: { name: NavigationIconName }) {
  return (
    <svg className="nav-icon" viewBox="0 0 24 24" aria-hidden="true">
      {ICON_PATHS[name].map((path) => <path key={path} d={path} />)}
    </svg>
  )
}

/** 계층형 탐색 메뉴와 단일 대시보드 화면을 제공하는 애플리케이션 셸이다. */
export default function App() {
  const [activeMenu, setActiveMenu] = useState<MenuId>('dashboard')
  const [isWorkMenuOpen, setIsWorkMenuOpen] = useState(false)

  return (
    <div className="app-layout">
      <aside className="side-navigation">
        <nav aria-label="주요 메뉴">
          <div className="navigation-section">
            <button
              className={`nav-item${activeMenu === 'dashboard' ? ' active' : ''}`}
              type="button"
              aria-current={activeMenu === 'dashboard' ? 'page' : undefined}
              onClick={() => setActiveMenu('dashboard')}
            >
              <NavigationIcon name="dashboard" />
              <span>대시보드</span>
            </button>
          </div>

          <div className="navigation-section">
            <button
              className={`nav-item${activeMenu === 'work' ? ' active' : ''}`}
              type="button"
              aria-expanded={isWorkMenuOpen}
              aria-controls="work-sub-navigation"
              onClick={() => {
                setActiveMenu('work')
                setIsWorkMenuOpen((isOpen) => !isOpen)
              }}
            >
              <NavigationIcon name="work" />
              <span>업무</span>
              <svg className={`menu-chevron${isWorkMenuOpen ? ' open' : ''}`} viewBox="0 0 24 24" aria-hidden="true">
                <path d="m9 6 6 6-6 6" />
              </svg>
            </button>

            {isWorkMenuOpen && (
              <div id="work-sub-navigation" className="sub-navigation" aria-label="업무 하위 메뉴">
                <button
                  className={`nav-item sub-item${activeMenu === 'work-list' ? ' active' : ''}`}
                  type="button"
                  onClick={() => setActiveMenu('work-list')}
                >
                  <NavigationIcon name="list" />
                  <span>업무 목록</span>
                </button>
                <button
                  className={`nav-item sub-item${activeMenu === 'work-board' ? ' active' : ''}`}
                  type="button"
                  onClick={() => setActiveMenu('work-board')}
                >
                  <NavigationIcon name="board" />
                  <span>업무 보드</span>
                </button>
              </div>
            )}

            <button
              className={`nav-item${activeMenu === 'schedule' ? ' active' : ''}`}
              type="button"
              onClick={() => setActiveMenu('schedule')}
            >
              <NavigationIcon name="schedule" />
              <span>일정</span>
            </button>
          </div>

          <div className="navigation-section">
            <button
              className={`nav-item${activeMenu === 'settings' ? ' active' : ''}`}
              type="button"
              onClick={() => setActiveMenu('settings')}
            >
              <NavigationIcon name="settings" />
              <span>설정</span>
            </button>
          </div>
        </nav>
      </aside>

      <main className="main-content" aria-labelledby="dashboard-title">
        <header className="dashboard-header">
          <div className="dashboard-title-row">
            <h1 id="dashboard-title">대시보드</h1>
            <span className="eyebrow">DASHBOARD</span>
          </div>
        </header>
      </main>
    </div>
  )
}
