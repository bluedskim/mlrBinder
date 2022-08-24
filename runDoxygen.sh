# sudo apt install doxygen graphviz	필요 프로그램 설치
# doxygen -g Doxyfile을 새로 생성할 때만 실행

# 문서 생성하기
doxygen Doxyfile

# html 보기
google-chrome ./doxyOut/html/index.html
# pdf생성할 때
# 해당 라이브러리를 설치 : sudo apt install texlive-full
# pdf생성
# doxyOut/lagex/make
