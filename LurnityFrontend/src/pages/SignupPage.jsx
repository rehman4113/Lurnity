import { GrSteps } from "react-icons/gr";
import { Link } from "react-router";
import { useForm } from "react-hook-form";

const SignupPage = () => {
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm();

  const onSubmit = (data) => {
    console.log("Form submitted", data);
  };

  const password = watch("password");

  return (
    <div className="relative flex items-center justify-center min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8 overflow-hidden">
      <div className="absolute -top-24 right-0 h-72 w-72 md:h-96 md:w-96 rounded-full bg-blue-500/20 blur-3xl pointer-events-none "></div>
      <div className="absolute -bottom-1/4 -left-1/4 h-1/2 w-1/2 rounded-full bg-blue-300/20 blur-3xl pointer-events-none"></div>

      <div className="z-10 flex w-full max-w-md flex-col items-center">
        <div className="mb-4 place-items-center">
          <GrSteps size={48} />
          <h1 className="mt-2 text-3xl font-bold text-gray-900">
            Create your account
          </h1>
          <p className="mt-2 text-gray-600">
            Start your learning journey with us.
          </p>
        </div>

        <div className="w-full rounded-2xl bg-white/70 p-8 backdrop-blur-lg border border-gray-200/80 shadow-lg">
          <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>
            <div className="grid grid-cols-1 gap-y-5 gap-x-4 sm:grid-cols-2">
              <div>
                <label
                  htmlFor="firstName"
                  className="block text-sm font-medium text-gray-700"
                >
                  First name
                </label>
                <div className="mt-1">
                  <input
                    type="text"
                    placeholder="Enter your first name"
                    {...register("firstName", {
                      required: "First name is required",
                    })}
                    className="block w-full rounded-lg border-1 border-gray-300 bg-gray-100/50 p-3 text-gray-900 placeholder-gray-400 transition duration-300 outline-none focus:ring focus:ring-gray-900/80"
                  />
                  {errors.firstName && (
                    <p className="text-xs text-red-600 mt-1">
                      {errors.firstName?.message}
                    </p>
                  )}
                </div>
              </div>
              <div>
                <label
                  htmlFor="lastName"
                  className="block text-sm font-medium text-gray-700"
                >
                  Last name
                </label>
                <div className="mt-1">
                  <input
                    type="text"
                    placeholder="Enter your last name"
                    {...register("lastName", {
                      required: "Last name is required",
                    })}
                    className="block w-full rounded-lg border-1 border-gray-300 bg-gray-100/50 p-3 text-gray-900 placeholder-gray-400 transition duration-300 outline-none focus:ring focus:ring-gray-900/80"
                  />
                  {errors.lastName && (
                    <p className="text-xs text-red-600 mt-1">
                      {errors.lastName?.message}
                    </p>
                  )}
                </div>
              </div>
            </div>

            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700"
              >
                Email address
              </label>
              <div className="mt-1">
                <input
                  type="email"
                  placeholder="Enter your email"
                  {...register("email", {
                    required: "Please enter your email",
                  })}
                  className="block w-full rounded-lg border-1 border-gray-300 bg-gray-100/50 p-3 text-gray-900 placeholder-gray-400 transition duration-300 outline-none focus:ring focus:ring-gray-900/80"
                />
                {errors.email && (
                  <p className="text-xs text-red-600 mt-1">
                    {errors.email?.message}
                  </p>
                )}
              </div>
            </div>

            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700"
              >
                Password
              </label>
              <div className="mt-1">
                <input
                  type="password"
                  placeholder="Enter your password"
                  {...register("password", {
                    required: "Please choose a password",
                  })}
                  className="block w-full rounded-lg border-1 border-gray-300 bg-gray-100/50 p-3 text-gray-900 placeholder-gray-400 transition duration-300 outline-none focus:ring focus:ring-gray-900/80"
                />
                {errors.password && (
                  <p className="text-xs text-red-600 mt-1">
                    {errors.password.message}
                  </p>
                )}
              </div>
            </div>

            <div>
              <label
                htmlFor="confirmPassword"
                className="block text-sm font-medium text-gray-700"
              >
                Confirm password
              </label>
              <div className="mt-1">
                <input
                  type="password"
                  placeholder="Confirm your password"
                  {...register("confirmPassword", {
                    required: "Confirm your password",
                    validate: (value) =>
                      value === password || "Passwords do not match",
                  })}
                  className="block w-full rounded-lg border-1 border-gray-300 bg-gray-100/50 p-3 text-gray-900 placeholder-gray-400 transition duration-300 outline-none focus:ring focus:ring-gray-900/80"
                />
                {errors.confirmPassword && (
                  <p className="text-xs text-red-600 mt-1">
                    {errors.confirmPassword.message}
                  </p>
                )}
              </div>
            </div>

            <div>
              <button
                type="submit"
                className="flex w-full cursor-pointer justify-center rounded-lg bg-blue-600 px-3 py-3.5 text-sm font-semibold text-white shadow-lg transition duration-300 hover:bg-blue-700 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
              >
                Sign Up
              </button>
            </div>
          </form>
        </div>

        <p className="mt-4 text-center text-sm text-gray-600">
          Already have an account?{" "}
          <Link
            to="/"
            className="font-medium text-blue-600 hover:text-blue-500"
          >
            Log in
          </Link>
        </p>
      </div>
    </div>
  );
};

export default SignupPage;
