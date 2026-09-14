import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import App from './App'

describe('App', () => {
  it('대시보드 제목과 선택된 메뉴를 표시한다', () => {
    render(<App />)

    expect(screen.getByRole('button', { name: '대시보드' })).toHaveAttribute(
      'aria-current',
      'page',
    )
    expect(screen.getByRole('heading', { name: '대시보드' })).toBeInTheDocument()
  })
})
