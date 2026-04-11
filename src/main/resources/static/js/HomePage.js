// 簡單範例：搜尋框按 Enter 觸發搜尋
document.querySelector('.actions input').addEventListener('keypress', function(e) {
  if (e.key === 'Enter') {
    alert('搜尋: ' + this.value);
  }
});