const int EOF = -1;

int isFowardAtFirstBufferEnd();
int isFowardAtSecondBufferEnd();

void putForwardToSecondBufferBegin();
void putForwardToFirstBufferBegin();

void loadSecondBuffer();
void loadFirstBuffer();

void parse(int *forward) {
    switch(*forward++) {
    case EOF:
        if (isFowardAtFirstBufferEnd()) {
            loadSecondBuffer();
            putForwardToSecondBufferBegin();
        } else if (isFowardAtSecondBufferEnd()) {
            loadFirstBuffer();
            putForwardToFirstBufferBegin();
        } else {
            break;
        }
    }
}
