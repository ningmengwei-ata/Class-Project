#include <stdlib.h>
#include <stdio.h>
#include <sys/time.h>
#include <assert.h>
#define n 4096
double A[n][n];
double B[n][n];
double C[n][n];
float tdiff(struct timeval *start,
struct timeval *end) {
return (end->tv_sec-start->tv_sec) +
1e-6*(end->tv_usec-start->tv_usec);
}
int main(int argc, const char *argv[]){
assert(argc==2);
int s = atoi(argv[1]);
if (s < 1 || s > 4096) {
printf("Invalid input values.\n");
return -1; }
for (int i=0; i<n; ++i){
for (int j = 0; j<n; ++j){
A[i][j] = (double)rand() / (double)RAND_MAX;
B[i][j] = (double)rand() / (double)RAND_MAX;
C[i][j] = 0; } }
struct timeval start, end;
gettimeofday(&start, NULL);
for (int ih = 0; ih < n; ih += s)
for (int jh = 0; jh < n; jh += s)
for (int kh = 0; kh < n; kh += s)
for (int il = 0; il < s; ++il)
for (int kl = 0; kl < s; ++kl)
for (int jl = 0; jl < s; ++jl)
C[ih+il][jh+jl] += A[ih+il][kh+kl] * B[kh+kl][jh+jl];
gettimeofday(&end, NULL);
printf("%0.6f\n", tdiff(&start, &end));
return 0; }