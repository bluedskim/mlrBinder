# sudo apt install doxygen graphviz	필요 프로그램 설치
# doxygen -g Doxyfile을 새로 생성할 때만 실행
#
# 문서 생성하기
doxygen Doxyfile

# html 보기
#google-chrome ./doxyOut/html/index.html
# pdf생성할 때
# 해당 라이브러리를 설치 : sudo apt install texlive-full

# 한글 설정. 한글 출력은 되지만 일부 페이지가 누락되는 문제가 있음 -.- html이 제일 보기 좋고 혹은 rtf를 보세요~
sed -i 's/usepackage\[T1\]{fontenc}/usepackage\[T1\]{kotex}/g' doxyOut/latex/refman.tex

# pdf생성
# doxyOut/latex/make
