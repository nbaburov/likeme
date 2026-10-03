/* eslint-disable @next/next/no-img-element */
"use client";
import { Dispatch, SetStateAction, useEffect, useRef, useState } from "react";
import { useAnimate, motion, AnimationScope } from "framer-motion";
import { FiMenu, FiUser } from "react-icons/fi";
import useMeasure from "react-use-measure";
import { PiHeartHalfLight } from "react-icons/pi";
import Link from "next/link";
import { useAuth } from "@/hooks/useAuth";

const ClientNavBar = () => {
	const [hovered, setHovered] = useState(false);
	const [menuOpen, setMenuOpen] = useState(false);

	const [scope, animate] = useAnimate();
	const navRef = useRef<HTMLDivElement | null>(null);

	const handleMouseMove = ({ offsetX, offsetY, target }: MouseEvent) => {
		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-expect-error
		const isNavElement = [...target.classList].includes("glass-nav");

		if (isNavElement) {
			setHovered(true);

			const top = offsetY + "px";
			const left = offsetX + "px";

			animate(scope.current, { top, left }, { duration: 0 });
		} else {
			setHovered(false);
		}
	};

	useEffect(() => {
		navRef.current?.addEventListener("mousemove", handleMouseMove);

		return () =>
			navRef.current?.removeEventListener("mousemove", handleMouseMove);
	}, []);

	return (
		<nav
			ref={navRef}
			onMouseLeave={() => setHovered(false)}
			style={{
				cursor: hovered ? "none" : "auto",
			}}
			className="glass-nav fixed left-0 right-0 top-0 z-10 mx-auto max-w-6xl overflow-hidden border-[1px] border-white/10 bg-gradient-to-br from-white/20 to-white/5 backdrop-blur md:left-6 md:right-6 md:top-6 md:rounded-2xl"
		>
			<div className="glass-nav flex items-center justify-between px-5 py-5 font-medium">
				<Cursor hovered={hovered} scope={scope} />

				<Links />

				<Logo />

				<Buttons setMenuOpen={setMenuOpen} />
			</div>

			<MobileMenu menuOpen={menuOpen} />
		</nav>
	);
};

const Cursor = ({
	hovered,
	scope,
}: {
	hovered: boolean;
	scope: AnimationScope<unknown>;
}) => {
	return (
		<motion.span
			initial={false}
			animate={{
				opacity: hovered ? 1 : 0,
				transform: `scale(${
					hovered ? 1 : 0
				}) translateX(-50%) translateY(-50%)`,
			}}
			transition={{ duration: 0.15 }}
			ref={scope as React.Ref<HTMLSpanElement>}
			className="pointer-events-none absolute z-0 grid h-[50px] w-[50px] origin-[0px_0px] place-content-center rounded-full bg-gradient-to-br from-orange-600 from-40% to-rose-400 text-2xl"
		>
			<PiHeartHalfLight className="text-white" />
		</motion.span>
	);
};

const Logo = () => (
	<span className="pointer-events-none relative left-0 top-[50%] z-10 text-4xl font-black text-white mix-blend-overlay md:absolute md:left-[50%] md:-translate-x-[50%] md:-translate-y-[50%]">
		<img src="/logo-heart.png" alt="LikeMe" className="w-16 h-16" />
	</span>
);

const Links = () => (
	<div className="hidden items-center gap-2 md:flex">
		<GlassLink text="How It Works" link="/#how-it-works" />
		<GlassLink text="Influencers" link="/influencers" />
		<GlassLink text="Contact" link="/contact" />
	</div>
);

const GlassLink = ({ text, link }: { text: string; link: string }) => {
	return (
		<a
			href={link}
			className="group relative scale-100 overflow-hidden rounded-lg px-4 py-2 transition-transform hover:scale-105 active:scale-95"
		>
			<span className="relative z-10 text-likeme-text transition-colors group-hover:text-likeme-primary">
				{text}
			</span>
			<span className="absolute inset-0 z-0 bg-gradient-to-br from-white/20 to-white/5 opacity-0 transition-opacity group-hover:opacity-100" />
		</a>
	);
};

const TextLink = ({ text, link }: { text: string; link: string }) => {
	return (
		<a
			href={link}
			className="text-likeme-text transition-colors hover:text-likeme-primary"
		>
			{text}
		</a>
	);
};

const Buttons = ({
	setMenuOpen,
}: {
	setMenuOpen: Dispatch<SetStateAction<boolean>>;
}) => {
	const { isAuthenticated, getUserRole } = useAuth();
	const [isLoggedIn, setIsLoggedIn] = useState(false);
	const [userRole, setUserRole] = useState<string | null>(null);

	useEffect(() => {
		const checkAuth = async () => {
			const authStatus = await isAuthenticated();
			setIsLoggedIn(authStatus);
			if (authStatus) {
				setUserRole(getUserRole());
			}
		};
		checkAuth();
	}, [isAuthenticated, getUserRole]);

	return (
		<div className="flex items-center gap-4">
			<div className="hidden md:block">
				{isLoggedIn ? (
					<ProfileButton userRole={userRole} />
				) : (
					<SignInButton />
				)}
			</div>

			{!isLoggedIn && (
				<Link
					href="/sign-up"
					className="relative scale-100 overflow-hidden rounded-lg bg-gradient-to-br from-likeme-accent to-likeme-accent px-4 py-2 font-medium text-white transition-transform hover:scale-105 active:scale-95"
				>
					Sign Up
				</Link>
			)}

			<button
				onClick={() => setMenuOpen((pv) => !pv)}
				className="ml-2 block scale-100 text-3xl text-likeme-text transition-all hover:scale-105 hover:text-likeme-primary active:scale-95 md:hidden"
			>
				<FiMenu />
			</button>
		</div>
	);
};

const SignInButton = () => {
	return (
		<Link
			href="/sign-in"
			className="group relative scale-100 overflow-hidden rounded-lg px-4 py-2 transition-transform hover:scale-105 active:scale-95"
		>
			<span className="relative z-10 text-likeme-texttransition-colors group-hover:text-likeme-primary">
				Sign in
			</span>
			<span className="absolute inset-0 z-0 bg-gradient-to-br from-white/20 to-white/5 opacity-0 transition-opacity group-hover:opacity-100" />
		</Link>
	);
};

const ProfileButton = ({ userRole }: { userRole: string | null }) => {
	const getProfileLink = () => {
		switch (userRole) {
			case "ADMIN":
				return "/admin/settings";
			case "INFLUENCER":
				return "/influencer/settings";
			default:
				return "/account/settings";
		}
	};

	return (
		<Link
			href={getProfileLink()}
			className="group relative scale-100 overflow-hidden rounded-lg px-4 py-2 transition-transform hover:scale-105 active:scale-95 flex items-center gap-2"
		>
			<div className="w-8 h-8 rounded-full bg-gradient-to-br from-likeme-accent to-likeme-accent flex items-center justify-center">
				<FiUser className="text-white" />
			</div>
			<span className="relative z-10 text-likeme-text transition-colors group-hover:text-likeme-primary">
				Profile
			</span>
			<span className="absolute inset-0 z-0 bg-gradient-to-br from-white/20 to-white/5 opacity-0 transition-opacity group-hover:opacity-100" />
		</Link>
	);
};

const MobileMenu = ({ menuOpen }: { menuOpen: boolean }) => {
	const [ref, { height }] = useMeasure();
	const { isAuthenticated, getUserRole } = useAuth();
	const [isLoggedIn, setIsLoggedIn] = useState(false);
	const [userRole, setUserRole] = useState<string | null>(null);

	useEffect(() => {
		const checkAuth = async () => {
			const authStatus = await isAuthenticated();
			setIsLoggedIn(authStatus);
			if (authStatus) {
				setUserRole(getUserRole());
			}
		};
		checkAuth();
	}, [isAuthenticated, getUserRole]);

	return (
		<motion.div
			initial={false}
			animate={{
				height: menuOpen ? height : "0px",
				transform: `scale(${menuOpen ? 1 : 0})`,
			}}
			className="block overflow-hidden md:hidden"
		>
			<div
				ref={ref}
				className="flex items-center justify-between px-4 pb-4"
			>
				<div className="flex items-center gap-4">
					<TextLink text="How It Works" link="/#how-it-works" />
					<TextLink text="Influencers" link="/influencers" />
					<TextLink text="Contact" link="/contact" />
				</div>
				{isLoggedIn ? (
					<ProfileButton userRole={userRole} />
				) : (
					<SignInButton />
				)}
			</div>
		</motion.div>
	);
};

export default ClientNavBar;
