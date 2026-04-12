# sudo apt install doxygen graphviz	# install required tools
# Run only when regenerating with: doxygen -g Doxyfile
#
# Generate documentation
doxygen Doxyfile

# View HTML
#google-chrome ./doxyOut/html/index.html
# When generating PDF
# Install TeX: sudo apt install texlive-full

# Korean (kotex) tweak: output works but some pages may be missing -.-
# Prefer HTML, or RTF.
sed -i 's/usepackage\[T1\]{fontenc}/usepackage\[T1\]{kotex}/g' doxyOut/latex/refman.tex

# Generate PDF
# doxyOut/latex/make
