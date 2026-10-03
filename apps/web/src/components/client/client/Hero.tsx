"use client";
import { useAnimate } from "framer-motion";
import React, { MouseEventHandler, ReactNode, useRef } from "react";
import { motion } from "framer-motion";
import { FiArrowDownCircle } from "react-icons/fi";

export const ImageTrailHero = () => {
	return (
		<MouseImageTrail
			renderImageBuffer={50}
			rotationRange={25}
			images={[
				"https://picsum.photos/seed/i/200/300",
				"https://picsum.photos/seed/t/200/300",
				"https://picsum.photos/seed/gerg/200/300",
				"https://picsum.photos/seed/egrg/200/300",
				"https://picsum.photos/seed/rege/200/300",
				"https://picsum.photos/seed/mj/200/300",
				"https://picsum.photos/seed/wfwe/200/300",
				"https://picsum.photos/seed/fgvr/200/300",
				"https://picsum.photos/seed/bwebf/200/300",
				"https://picsum.photos/seed/woman/200/300",
				"https://picsum.photos/seed/niko/200/300",
				"https://picsum.photos/seed/stefi/200/300",
				"https://picsum.photos/seed/hfbhbfwb/200/300",
				"https://picsum.photos/seed/ge/200/300",
			]}
		>
			<section className="h-screen bg-slate-200">
				<Copy />
				<WatermarkWrapper />
			</section>
		</MouseImageTrail>
	);
};

const Copy = () => {
	return (
		<div className="absolute bottom-0 left-0 right-0 z-[999999]">
			<div className="mx-auto flex max-w-7xl items-end justify-between p-4 md:p-8">
				<div>
					<h1 className="mb-6 max-w-4xl text-6xl font-black leading-[1.1] text-likeme-text md:text-8xl">
						Become Famous with{" "}
						<span className="text-likeme-primary">LikeMe</span>
					</h1>
					<p className="max-w-xl text-likeme-text md:text-lg">
						Buy exposure, social media likes, followers, comments
						from real celebrities. Elevate your online presence and
						engage with your audience like never before. Experience
						the power of authentic interactions and watch your
						influence grow.
					</p>
				</div>
				<FiArrowDownCircle className="hidden text-8xl text-orange-500 md:block" />
			</div>
		</div>
	);
};

const WatermarkWrapper = () => {
	return (
		<>
			<Watermark text="Get exposure" />
			<Watermark text="Become Famous" reverse />
			<Watermark text="Get Likes" />
			<Watermark text="Show off" reverse />
			<Watermark text="Impress everyone" />
			<Watermark text="Get exposure" reverse />
			<Watermark text="Make a gift" />
			<Watermark text="Be someone" reverse />
		</>
	);
};

const Watermark = ({ reverse, text }: { reverse?: boolean; text: string }) => (
	<div className="flex -translate-y-12 select-none overflow-hidden bg-gradient-to-tr bg-likeme-third">
		<TranslateWrapper reverse={reverse}>
			<span className="w-fit text-orange-200 whitespace-nowrap text-[20vmax] font-black uppercase leading-[0.75]">
				{text}
			</span>
		</TranslateWrapper>
		<TranslateWrapper reverse={reverse}>
			<span className="ml-48 w-fit whitespace-nowrap text-[20vmax] font-black uppercase leading-[0.75] text-red-300">
				{text}
			</span>
		</TranslateWrapper>
	</div>
);

const TranslateWrapper = ({
	children,
	reverse,
}: {
	children: ReactNode;
	reverse?: boolean;
}) => {
	return (
		<motion.div
			initial={{ translateX: reverse ? "-100%" : "0%" }}
			animate={{ translateX: reverse ? "0%" : "-100%" }}
			transition={{ duration: 75, repeat: Infinity, ease: "linear" }}
			className="flex"
		>
			{children}
		</motion.div>
	);
};

const MouseImageTrail = ({
	children,
	// List of image sources
	images,
	// Will render a new image every X pixels between mouse moves
	renderImageBuffer,
	// images will be rotated at a random number between zero and rotationRange,
	// alternating between a positive and negative rotation
	rotationRange,
}: {
	children: ReactNode;
	images: string[];
	renderImageBuffer: number;
	rotationRange: number;
}) => {
	const [scope, animate] = useAnimate();

	const lastRenderPosition = useRef({ x: 0, y: 0 });
	const imageRenderCount = useRef(0);

	const handleMouseMove: MouseEventHandler<HTMLDivElement> = (e) => {
		const { clientX, clientY } = e;

		const distance = calculateDistance(
			clientX,
			clientY,
			lastRenderPosition.current.x,
			lastRenderPosition.current.y
		);

		if (distance >= renderImageBuffer) {
			lastRenderPosition.current.x = clientX;
			lastRenderPosition.current.y = clientY;

			renderNextImage();
		}
	};

	const calculateDistance = (
		x1: number,
		y1: number,
		x2: number,
		y2: number
	) => {
		const deltaX = x2 - x1;
		const deltaY = y2 - y1;

		// Using the Pythagorean theorem to calculate the distance
		const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

		return distance;
	};

	const renderNextImage = () => {
		const imageIndex = imageRenderCount.current % images.length;
		const selector = `[data-mouse-move-index="${imageIndex}"]`;

		const el = document.querySelector(selector) as HTMLElement;

		el.style.top = `${lastRenderPosition.current.y}px`;
		el.style.left = `${lastRenderPosition.current.x}px`;
		el.style.zIndex = imageRenderCount.current.toString();

		const rotation = Math.random() * rotationRange;

		animate(
			selector,
			{
				opacity: [0, 1],
				transform: [
					`translate(-50%, -25%) scale(0.5) ${
						imageIndex % 2
							? `rotate(${rotation}deg)`
							: `rotate(-${rotation}deg)`
					}`,
					`translate(-50%, -50%) scale(1) ${
						imageIndex % 2
							? `rotate(-${rotation}deg)`
							: `rotate(${rotation}deg)`
					}`,
				],
			},
			{ type: "spring", damping: 15, stiffness: 200 }
		);

		animate(
			selector,
			{
				opacity: [1, 0],
			},
			{ ease: "linear", duration: 0.5, delay: 1 }
		);

		imageRenderCount.current = imageRenderCount.current + 1;
	};

	return (
		<div
			ref={scope}
			className="relative overflow-hidden"
			onMouseMove={handleMouseMove}
		>
			{children}

			{images.map((img, index) => (
				<img
					className="pointer-events-none absolute left-0 top-0 h-36 w-auto rounded-xl border-2 border-likeme-primary bg-likeme-primary object-cover opacity-0"
					src={img}
					alt={`Mouse move image ${index}`}
					key={index}
					data-mouse-move-index={index}
				/>
			))}
		</div>
	);
};
