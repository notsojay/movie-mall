import re
import sys
from pathlib import Path


def calculate_average_times(log_file_path):
    total_servlet_time = 0
    total_jdbc_time = 0
    servlet_count = 0
    jdbc_count = 0

    with open(log_file_path, 'r') as file:
        for line in file:
            servlet_match = re.search(r'Servlet Time: (\d+)ns', line)
            jdbc_match = re.search(r'JDBC Time: (\d+)ns', line)

            if servlet_match:
                servlet_time = int(servlet_match.group(1))
                total_servlet_time += servlet_time
                servlet_count += 1

            if jdbc_match:
                jdbc_time = int(jdbc_match.group(1))
                total_jdbc_time += jdbc_time
                jdbc_count += 1

    if servlet_count > 0:
        avg_servlet_time = total_servlet_time / servlet_count
        print(f"Average Servlet Time (TS): {avg_servlet_time} ns")
    else:
        print("No valid servlet time entries found.")

    if jdbc_count > 0:
        avg_jdbc_time = total_jdbc_time / jdbc_count
        print(f"Average JDBC Time (TJ): {avg_jdbc_time} ns")
    else:
        print("No valid JDBC time entries found.")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python script.py path_to_log_file")
    else:
        log_file_path = sys.argv[1]
        log_file = Path(log_file_path)
        calculate_average_times(log_file)
