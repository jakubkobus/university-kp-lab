#ifndef ARABRZYMEXCEPTION_HPP
#define ARABRZYMEXCEPTION_HPP

#include <exception>
#include <string>

class ArabRzymException : public std::exception {
  std::string msg_;

  public:
    explicit ArabRzymException(const std::string& msg) : msg_(msg) {}
    virtual const char* what() const throw() { return msg_.c_str(); }
};

#endif // ARABRZYMEXCEPTION_HPP