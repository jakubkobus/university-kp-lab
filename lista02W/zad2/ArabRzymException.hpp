#ifndef ARABRZYMEXCEPTION_HPP
#define ARABRZYMEXCEPTION_HPP

#include <exception>
#include <string>

class ArabRzymException : public std::exception {
  std::string msg_;

  public:
    explicit ArabRzymException(const std::string &msg);
    virtual ~ArabRzymException() noexcept;
    virtual const char* what() const noexcept;
};

#endif // ARABRZYMEXCEPTION_HPP