import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/students'
        body([
                name  : $(regex('[A-Za-z ]+')),
                email : $(regex('.+@.+')),
                course: $(regex('[A-Za-z ]+'))
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 201
        body([
                id    : anyNumber(),
                name  : fromRequest().body('name'),
                email : fromRequest().body('email'),
                course: fromRequest().body('course')
        ])
        headers {
            contentType(applicationJson())
        }
    }
}
