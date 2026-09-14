import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import App from './App'

describe('App', () => {
  it('빈 대시보드 작업영역을 표시한다', () => {
    render(<App />)

    expect(screen.getByRole('button', { name: '대시보드' })).toHaveAttribute(
      'aria-current',
      'page',
    )
    expect(screen.getByLabelText('대시보드 작업영역')).toBeInTheDocument()
  })
})
